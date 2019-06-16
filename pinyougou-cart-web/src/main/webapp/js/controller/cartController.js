app.controller('cartController',function ($scope,cartService) {
        //查询
    $scope.findCartList=function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList=response;

                //每次查询完,执行合计
                $scope.totalValue= cartService.sum($scope.cartList);//求合计数
            }
        )
    }

    //添加
    $scope.addGoodsToCartList=function (itemId,num) {
        cartService.addGoodsToCartList(itemId,num).success(
            function (response) {
                if(response.success){
                    $scope.findCartList();//添加成功重新查询购物车列表
                }else{
                    alert(response.massage);
                }
            }
        )
    }


})