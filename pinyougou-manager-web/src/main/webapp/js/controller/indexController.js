app.controller('indexController',function ($scope,loginService) {

    $scope.getLoginName=function () {
        loginService.getLoginName().success(
            function (response) {
                $scope.LoginName=response.loginName;
            }
        )
    }

})