app.controller("payController", function ($scope, $location, cartService, payService) {

    $scope.getUsername = function () {
        cartService.getUsername().success(function (response) {
            $scope.username = response.username;
        });
    };

    // 生成二维码
    $scope.createNative = function () {

        // 接收地址栏中的支付日志id
          $scope.outTradeNo = $location.search()["outTradeNo"];

        payService.createNative($scope.outTradeNo).success(function (response) {

            if ("SUCCESS"==response.result_code) {
                // 显示
                $scope.money=(response.totalFee/100).toFixed(2);
                // 如果
                var qr = new QRious({
                    element:document.getElementById("qrious"),
                    level:"Q",
                    size:250,
                    value:response.code_url
                });
                // 查询支付状态
                 queryPayStatus($scope.outTradeNo);
            }else{
                alert("生成二维码失败");
            }
        });
    };
    
    // 查询支付状态
    queryPayStatus = function (outTradeNo) {
        payService.queryPayStatus(outTradeNo).success(function (response) {
            if (response.success){
                //跳转到支付成功提示页面
                location.href = "paysuccess.html#?money=" + $scope.money;
            } else {
                 if ("支付超时" == response.message){
                     // alert(response.message);
                     $scope.createNative();
                 } else {
                    //支付失败则跳转到支付失败提示页面
                     location.href = "payfail.html";
                 }
            }
        });
    };
       //回显 支付金额
     $scope.getMoney = function () {
         $scope.money = $location.search()["money"];
     };
});