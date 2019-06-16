app.controller('itemController',function ($scope,$location) {



        $scope.addNum=function (x) {
                $scope.Num=$scope.Num+x;
                if( $scope.Num<1){
                    $scope.Num=1;
                }
        }

        //记录用户选择单位规格
    $scope.specificationItems={};
        //用户选择规格
    $scope.selectSpecification=function (name,value) {

        $scope.specificationItems[name]=value;
        searchSku();

    }
        //判断某规格选项是否被用户选中

    $scope.isSelected=function (name,value) {
        if( $scope.specificationItems[name]==value){
            return true;
        }
        else{
            return false;
        }
    }

//    加载默认的sku
    $scope.loadSku=function () {
        var id=$location.search()['id'];
       for(var i=0;i<skuList.length;i++){
            if(id==skuList[i].id){
                $scope.sku=skuList[i];
                $scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
                return;
            }
       }
        $scope.sku=skuList[0];
        $scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
    }

    //匹配两个对象

    matchObject=function (map1,map2) {

        for(var k in map1){
            if(map1[k]!=map2[k]){
                return false;
            }

        }
        for(var k in map2){
            if(map2[k]!=map1[k]){
                return false;
            }

        }

        return true;

    }
    //查询sku

    searchSku=function () {

        for(var i=0;i<skuList.length;i++){

            if(matchObject( $scope.specificationItems,skuList[i].spec)){

                $scope.sku=skuList[i];
                return;
            }

        }

        $scope.sku={id:0,title:'--------',price:0};//没有匹配
    }
    $scope.addToCart=function(){
        alert('skuid:'+$scope.sku.id);
    }

})