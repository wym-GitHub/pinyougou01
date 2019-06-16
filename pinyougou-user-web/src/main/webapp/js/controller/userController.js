 //控制层 
app.controller('userController' ,function($scope ,userService){
	
	//注册用户
	$scope.reg=function () {

		//判断密码确认是否一致
		if($scope.entity.password!=$scope.password){
			alert("两次密码不一致,请重新输入");
			return;
		}

        userService.add($scope.entity,$scope.smscode).success(
        	function (response) {
        		if(response.success){
                    alert(response.massage)
                    $scope.entity={};
                    $scope.password="";
                    $scope.smscode="";
                }else{
                    alert(response.massage)

                }

            }
		)

    }


    //发送验证码

	$scope.sendCode=function () {
		if($scope.entity.phone==null||$scope.entity.phone==""){
			alert("手机号不能为空");
			return;
		}
        userService.sendCode($scope.entity.phone).success(
        	function (response) {
				if(response.success){
					alert(response.massage);

				}else{
                    alert(response.massage);

                }

            }
		)

    }
});	
