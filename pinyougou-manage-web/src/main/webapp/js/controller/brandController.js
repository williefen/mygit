//注册处理器
app.controller("brandController", function ($scope, $http, $controller, brandService) {

    //继承某个controller；参数1：要继承的controller名称;参数2：上下文
    $controller("baseController", {$scope:$scope});

    //获取品牌所有数据
    $scope.findAll = function () {
        brandService.findAll().success(function (response) {
            //将返回的数据设置到一个上下文变量
            $scope.list = response;
        }).error(function () {
            alert("获取品牌列表失败！");
        });

    };

    $scope.findPage = function (page, rows) {
        brandService.findPage(page, rows).success(function (response) {

            //修改列表为最新的返回数据列表
            $scope.list = response.rows;

            //设置总记录数
            $scope.paginationConf.totalItems = response.total;
        });

    };

    //保存品牌数据
    $scope.save = function () {

        var obj;
        if ($scope.entity.id != null) {
            //修改
            obj = brandService.update($scope.entity);
        } else {
            obj = brandService.add($scope.entity);
        }

        obj.success(function (response) {
            if(response.success){
                //刷新列表
                $scope.reloadList();
            } else {
                alert(response.message);
            }
        });

    };

    //根据id查询
    $scope.findOne = function (id) {

        brandService.findOne(id).success(function (response) {
            $scope.entity = response;
        });

    };


    //删除
    $scope.delete = function () {
        if($scope.selectedIds.length < 1){
            alert("请先选择要删除的记录");
            return;//不再执行后面的代码
        }
        //confirm 如果点击了弹出的对话框中的 确定则返回true，否则返回false
        if(confirm("确定要删除选择了的那些记录吗？")){
            brandService.delete($scope.selectedIds).success(function (response) {
                if(response.success){
                    //刷新列表
                    $scope.reloadList();
                    //清空当时选择的id
                    $scope.selectedIds = [];
                } else {
                    alert(response.message);
                }
            }).error(function () {
                alert("删除失败！");
            });
        }

    };

    //初始化查询对象;如果不初始化在后台则不认为它是一个json对象转换的时候则报错
    $scope.searchEntity = {};

    //根据条件分页查询数据
    $scope.search = function (page, rows) {

        //post的参数1：是一个地址；在地址中携带的参数在后台的任何映射都可以接收到
        //post的参数2：提交的表单项；只能使用postMapping接收到
        brandService.search(page, rows, $scope.searchEntity).success(function (response) {
            //设置列表
            $scope.list = response.rows;

            //更新总记录数
            $scope.paginationConf.totalItems = response.total;
        });
    };
});
