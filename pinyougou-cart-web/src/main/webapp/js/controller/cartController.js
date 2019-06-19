app.controller('cartController', function ($scope, cartService) {
    //查询
    $scope.findCartList = function () {
        cartService.findCartList().success(
            function (response) {
                $scope.cartList = response;

                //每次查询完,执行合计
                $scope.totalValue = cartService.sum($scope.cartList);//求合计数
            }
        )
    }

    //添加
    $scope.addGoodsToCartList = function (itemId, num) {
        cartService.addGoodsToCartList(itemId, num).success(
            function (response) {
                if (response.success) {
                    $scope.findCartList();//添加成功重新查询购物车列表
                } else {
                    alert(response.massage);
                }
            }
        )
    }

    //根据登录用户id查询,联系人地址
    $scope.findListByUserId = function () {
        cartService.findListByUserId().success(
            function (response) {
                $scope.addressList = response;
                //判断是否是默认的地址
                for (var i = 0; i < $scope.addressList.length; i++) {
                    if ($scope.addressList[i].isDefault == '1') {
                        $scope.address = $scope.addressList[i];
                        return;
                    }

                }

            }
        )
    }

    //定义一个变量接收选中的地址
    $scope.selectAddress = function (address) {
        $scope.address = address;
    }

    //判断自己是否是当前选中对象
    $scope.isSelectedAddress = function (address) {
        if ($scope.address == address) {
            return true;
        } else {
            return false;
        }
    }

    //支付方式的选择
    //定义订单对象
    $scope.order = {paymentType: '1'}

    $scope.selectPayType = function (type) {
        $scope.order.paymentType = type;
    }
    //保存订单

    $scope.submitOrder=function () {
        $scope.order.receiverAreaName=$scope.address.address;//地址
        $scope.order.receiverMobile=$scope.address.mobile;//手机
        $scope.order.receiver=$scope.address.contact;//联系人
        cartService.submitOrder($scope.order).success(
            function (response) {
                if(response.success){
                    //保存成功跳转到支付页面
                    //判断支付方式,如果是微信支付就跳转到支付页
                    if($scope.order.paymentType=='1'){
                        location.href="pay.html";
                    }else{//如果货到付款，跳转到提示页面
                        location.href="paysuccess.html";
                    }

                }else{
                    alert(response.massage); //也可以跳转到提示页面
                }
            }
        )

    }

})