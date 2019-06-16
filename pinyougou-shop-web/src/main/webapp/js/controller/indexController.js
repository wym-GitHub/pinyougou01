app.controller('indexController',function ($scope,indexService) {

    //查询登录的商家名

    $scope.findLoginName=function () {
        indexService.findLoginName().success(
            function (response) {
                $scope.loginName=response.loginName;
            }
        )
    }

})