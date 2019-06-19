app.service('cartService',function ($http) {

        //购物车列表
    this.findCartList=function () {
        return $http.get('cart/findCartList.do');
    }
    this.addGoodsToCartList=function (itemId,num) {
        return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+"&num="+num);
    }

    //合计数
    this.sum=function (cartList) {

        var totalValue={'totalNum':0,'totalMoney':0.00};//定义合计实体

        for(var i=0;i<cartList.length;i++){
            var cart=cartList[i];
            for(var j=0;j<cartList[i].orderItemList.length;j++){
                var orderItem=cartList[i].orderItemList[j];//购物车明细
                totalValue.totalNum+=orderItem.num;
                totalValue.totalMoney+=orderItem.totalFee;

            }
        }
        return totalValue;
    }
    //查询地址列表
    this.findListByUserId=function () {
        return $http.get("address/findListByLoginUser.do");
    }
    //保存订单
    this.submitOrder=function (order) {
        return $http.post('order/add.do',order);
    }

})