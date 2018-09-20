app.service("cartService", function ($http) {
    this.getUsername = function () {
        return $http.get("cart/getUsername.do?t=" + Math.random());
    };

    this.findCartList = function () {
        return $http.get("cart/findCartList.do?t=" + Math.random());
    };

    this.addCartToCartList = function (itemId, num) {

        return $http.get("cart/addCartToCartList.do?itemId=" + itemId + "&num=" + num);
    };

    this.sumTotalValue = function (cartList) {
        var totalValue = {"totalNum":0, "totalMoney":0.0};

        for (var i = 0; i < cartList.length; i++) {
            var cart = cartList[i];
            for (var j = 0; j < cart.orderItemList.length; j++) {
                var orderItem = cart.orderItemList[j];
                totalValue.totalNum += orderItem.num;
                totalValue.totalMoney += orderItem.totalFee;
            }
        }
        return totalValue;
    };

});