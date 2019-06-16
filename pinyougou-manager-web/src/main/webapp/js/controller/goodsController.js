 //控制层 
app.controller('goodsController' ,function($scope,$controller,goodsService,itemCatService,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;
                //展示富文本信息
                editor.html($scope.entity.goodsDesc.introduction);
                editor.readonly(true);//富文本设置只读,字母都要小写
                //展示图片
                $scope.entity.goodsDesc.itemImages=JSON.parse(  $scope.entity.goodsDesc.itemImages);
                $scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
                //展示扩展属性
                $scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
                //展示sku列表
                for(var i=0;i<response.itemList.length;i++){
                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
                }
                //商品分类显示
                //查询一级分类
                itemCatService.findByParentId(0).success(
                    function (response) {
                        $scope.itemCat1List=response;
                    }
                )
                //查询二级分类
                itemCatService.findByParentId($scope.entity.goods.category1Id).success(
                    function (response) {
                        $scope.itemCat2List=response;
                    }
                )
                //查询三级分类

                itemCatService.findByParentId($scope.entity.goods.category2Id).success(
                    function (response) {
                        $scope.itemCat3List=response;
                    }
                )
                //查询品牌

                typeTemplateService.findOne($scope.entity.goods.typeTemplateId).success(
                    function (response) {
                        $scope.typeTemplateBrandList=JSON.parse(response.brandIds);


                    }
                )
                typeTemplateService.findSpecList($scope.entity.goods.typeTemplateId).success(
                    function (response) {
                        $scope.specList=response;


                    }
                )

            }
		);				
	}
    //显示规格对勾
    $scope.checkAttributeValue=function (attributeName,attributeValue) {
        var specItems=$scope.entity.goodsDesc.specificationItems;
        var object=$scope.searchObjectByKey(specItems,"attributeName",attributeName);
        if(object==null){
            return false;

        }else{
            if(object.attributeValue.indexOf(attributeValue)>=0){
                return true;
            }
            else{
                return false;
            }
        }


    }
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.ids ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.ids=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	$scope.status=['未审核','已审核','驳回']
	$scope.itemCatList=[];
	$scope.findItemCatList=function () {
        itemCatService.findAll().success(
        	function (response) {

				for(var i=0;i<response.length;i++){
                    $scope.itemCatList[response[i].id]=response[i].name;
				}

            }
		)
    }
    //商品状态审核
	$scope.updateStatus=function (status) {
		goodsService.updateStatus($scope.ids,status).success(
			function (response) {
				if(response.success){
					$scope.reloadList();
				}
				else{
					alert(response.massage);

				}
            }
		)
    }

});	
