app.controller("payController",function ($scope, payService,$location) {

    //本地生成二维码
    $scope.createNative=function () {
        payService.createNative().success(
            function (response) {
                $scope.money=(response.total_fee/100)//支付总金额
                $scope.out_trade_no= response.out_trade_no;//订单号
                //二维码
                var qr=new QRious({

                    element:document.getElementById('qrious'),
                    size:250,//尺寸
                    level:'H',//过滤级别
                    value:response.code_url//二维码路径
                })
                queryPayStatus(response.out_trade_no);
            }
        )
    }

    //查询支付状态
    queryPayStatus=function (out_trade_no) {

        payService.queryPayStatus(out_trade_no).success(
            function (response) {
                if(response.success){
                    //支付成功
                    location.href="paysuccess.html#?money="+$scope.money;
                }else{
                    if(response.massage=="二维码超时"){
                        location.href="payTimeOut.html";
                    }else{
                        location.href="payfail.html";
                    }

                }
            }
        )
    }

    //获取参数,金额
    $scope.getMoney=function(){
        return $location.search()['money'];
    }
})