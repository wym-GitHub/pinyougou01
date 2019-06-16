 //控制层 
app.controller('contentController' ,function($scope,contentService){
	


	$scope.contentList=[]
	$scope.findContentCategoryList=function (id) {

        contentService.findContentCategoryList(id).success(
        	function (response) {

                $scope.contentList[id]=response;
            }
		)
		
    }
    $scope.keywords='';
    $scope.search=function () {
            if(keywords==''){
                return;
            }
        location.href="http://localhost:9104/search.html#?keywords="+$scope.keywords;
    }

});	
