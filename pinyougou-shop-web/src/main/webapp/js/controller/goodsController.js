 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,uploadService,itemCatService,typeTemplateService){
	
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
	$scope.findOne=function(){
		var id=$location.search()['id'];
		if(id==null){
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;

				//显示富文本
				editor.html($scope.entity.goodsDesc.introduction);
				//显示图片
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				//显示扩展属性
                $scope.entity.goodsDesc.customAttributeItems=JSON.parse( $scope.entity.goodsDesc.customAttributeItems);
                //显示sku
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				for(var i=0;i<$scope.entity.itemList.length;i++){

                    $scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				}

			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象
		$scope.entity.goodsDesc.introduction=editor.html();
		if($scope.entity.goods.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					$scope.entity={};
					editor.html("");
					location.href="goods.html";
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
	$scope.status=['未审核','已审核',"审核未通过",'关闭'];

	//上串图片
	$scope.uploadFile=function () {

        uploadService.uploadFile().success(
        	function (response){
        		if(response.success){
        			alert(response.massage);
                    $scope.image_entity.url=response.massage;

                }
            else{
                    alert(response.massage);
                }
            }

		).error(
			function () {
				alert("上传时发生错误");

            }
		)
    }

    //添加图片列表
	$scope.entity={goodsDesc:{itemImages:[],specificationItems:[]}};
	$scope.add_img_entity=function () {

        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    $scope.dele_img_entity=function (index) {
        $scope.entity.goodsDesc.itemImages.splice(index,1);

    }
    //查询一级列表
    $scope.itemCat1List=[];

	$scope.selectitemCat1List=function () {
        itemCatService.findByParentId(0).success(
        	function (response) {
				$scope.itemCat1List=response;

                $scope.itemCat3List=[];
            }
		)
    }

    //查询二级列表

    $scope.$watch('entity.goods.category1Id',function (newvalue,oldvalue) {
		//根据选择的值作为parentId查询下级
		if(newvalue==undefined){
			return;
		}
        itemCatService.findByParentId(newvalue).success(
        	function (response) {
                $scope.itemCat2List=response;

            }
		)
    })
	//查询三级分类列表

    $scope.$watch('entity.goods.category2Id',function (newvalue,oldvalue) {
        //根据选择的值作为parentId查询下级
        if(newvalue==undefined){
            return;
        }
        itemCatService.findByParentId(newvalue).success(
            function (response) {
                $scope.itemCat3List=response;

            }
        )
    })

	//根据三级分类选中的商品,查询模板id
	$scope.$watch('entity.goods.category3Id',function (newvalue,oldvalue) {
        if(newvalue==undefined){
            return;
        }
        itemCatService.findOne(newvalue).success(
        	function (response) {
				$scope.entity.goods.typeTemplateId=response.typeId;
            }
		)
    })

	//根据模板id查模板变,完成品牌下拉列表
	$scope.$watch('entity.goods.typeTemplateId',function (newvalue,oldvaue) {
        if(newvalue==undefined){
            return;
        }
        typeTemplateService.findOne(newvalue).success(
        	function (response) {
				$scope.brandOptionList=JSON.parse(response.brandIds);

				if($location.search()['id']==null){
                    $scope.entity.goodsDesc.customAttributeItems=JSON.parse(response.customAttributeItems);

                }


            }
		)

        typeTemplateService.findSpecList(newvalue).success(
        	function (response) {

				$scope.specList=response;
            }
		)
    })

	$scope.updateSpecAttribute=function ($event,name,value) {
		var object= $scope.searchObjectByKey($scope.entity.goodsDesc.specificationItems,'attributeName',name);
		if(object!=null){
			if($event.target.checked){
                object.attributeValue.push(value);
			}
			else{
                object.attributeValue.splice(object.attributeValue.indexOf(value),1);
                if( object.attributeValue.length==0){
                    $scope.entity.goodsDesc.specificationItems.splice( $scope.entity.goodsDesc.specificationItems.indexOf(object),1);
				}
			}
		}
		else{
            $scope.entity.goodsDesc.specificationItems.push({'attributeName':name,'attributeValue':[value]});
		}
    }

    //创建sku列表
	$scope.createItemList=function () {


			$scope.entity.itemList=[{spec:{},price:0,status:0,isDefault:0,num:9999}];

		var items=$scope.entity.goodsDesc.specificationItems;
		for(var i=0;i<items.length;i++){

            $scope.entity.itemList=addColumn($scope.entity.itemList,items[i].attributeName,items[i].attributeValue);
		}

    }

    addColumn=function (list,columnName,conlumnValues) {
		var newList=[];

		for(var i=0;i<list.length;i++){
			var oldRow=list[i];
			for (var j=0;j<conlumnValues.length;j++){
				var newRow=JSON.parse(JSON.stringify(oldRow));//深克隆
				newRow.spec[columnName]=conlumnValues[j];
                newList.push(newRow);
			}
		}
		return newList;

    }
    $scope.itemCatList=[];
    $scope.findItemCat=function () {

        itemCatService.findAll().success(
        	function (response) {
				for(var i=0;i<response.length;i++){

					$scope.itemCatList[response[i].id]=response[i].name;
				}
            }
		)

    }

    //规格状态是否打勾显示

	$scope.checkAttributeValue=function (key,value) {
        var specItems=$scope.entity.goodsDesc.specificationItems;
        var object= $scope.searchObjectByKey(specItems,"attributeName",key);
    	if(object==null){
    		return false


		}else{
    		if(object.attributeValue.indexOf(value)>=0){
    			return true;
			}
			else{
    			return false;
			}
		}

    }

});	
