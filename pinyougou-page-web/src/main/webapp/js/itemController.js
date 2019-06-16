//商品详细页（控制层）
app.controller('itemController',function($scope){
	$scope.num=1;
	//数量操作
	$scope.addNum=function(x){
		$scope.num=$scope.num+x;
		if($scope.num<1){
			$scope.num=1;
		}
	}

	$scope.specificationItems={};//记录用户选择的规格
	//用户选择规格
	$scope.selectSpecification=function(name,value){
		// alert(typeof(name));		
		$scope.specificationItems[name]=value;
		searchSku();//读取sku
	}	
	//判断某规格选项是否被用户选中
	$scope.isSelected=function(name,value){
		if($scope.specificationItems[name]==value){
			return true;
		}else{
			return false;
		}		
	}
	
	//加载默认SKU
	$scope.loadSku=function(){
		$scope.sku=skuList[0];		
		$scope.specificationItems= JSON.parse(JSON.stringify($scope.sku.spec)) ;
	}
	
	// SKU商品列表
	/*
		var skuList=[    	    
			{
			"id":1369284,
			"title":"老年机 移动3G 16G",
			"price":11,		    		
			"spec": {"网络":"移动3G","机身内存":"16G"}	
			} ,     		
			{
			"id":1369285,
			"title":"老年机 移动3G 64G",
			"price":12,		    		
			"spec": {"网络":"移动3G","机身内存":"64G"}	
			} ,     		
			{
			"id":1369286,
			"title":"老年机 移动4G 16G",
			"price":13,		    		
			"spec": {"网络":"移动4G","机身内存":"16G"}	
			} ,     		
			{
			"id":1369287,
			"title":"老年机 移动4G 64G",
			"price":14,		    		
			"spec": {"网络":"移动4G","机身内存":"64G"}	
			} ,     		
		];  
	*/

	
	//匹配两个对象
	matchObject=function(map1,map2){	
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
	
	
	//查询SKU
	searchSku=function(){
		for(var i=0;i<skuList.length;i++ ){
			if( matchObject(skuList[i].spec ,$scope.specificationItems ) ){
				$scope.sku=skuList[i];
				return ;
			}
		}
		$scope.sku={id:0,title:'--------',price:0};//如果没有匹配的		
	}

	//添加商品到购物车
	$scope.addToCart=function(){
		alert('skuid:'+$scope.sku.id);				
	}


});	