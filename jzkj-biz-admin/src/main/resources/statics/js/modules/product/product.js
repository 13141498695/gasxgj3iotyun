$(function () {
    $("#jqGrid").jqGrid({
        url: baseURL + '/sys/product/list',
        datatype: "json",
        colModel: [

            { label: '产品id', name: 'productId',  index: "productId",width: 45, key: true,hidden:true},
            { label: '产品名称', name: 'productName', width: 40 },
            { label: '产品版本', name: 'productVersion', sortable: false, width: 40 },
            { label: '产品类型', name: 'productType', width: 20 },
            { label: '公司', name: 'productCompany', width: 30 },
            { label: '公司id', name: 'deptId', width: 30 ,hidden:true},

            { label: '产品描述', name: 'productContext', width: 200 },

            /*	{ label: '联网方式', name: 'network', width: 100 },*/
			{ label: '所属行业', name: 'productType', width: 30, formatter: function(value, options, row){
				return value === 0 ? 
					'<span class="label label-danger">3G/4G/5G</span>' : value === 1 ?
					'<span class="label label-success">蜂巢网络</span>' : value === 2 ?
							'<span class="label label-success">WIFI</span>' :
								'<span class="label label-success">其他</span>';
			}},
			/*{ label: '产品状态', name: 'status', width: 100 },*/
			{ label: '产品状态', name: 'productStatus', width: 30, formatter: function(value, options, row){
				return value === 0 ? 
					'<span class="label label-danger">未发布</span>' : value === 1 ?
					'<span class="label label-danger">发布中</span>' : value === 2 ?
					'<span class="label label-success">已发布</span>':
							'<span class="label label-danger">下线</span>';
			}},
			{ label: '创建时间', name: 'createTime', width: 85},
            { label: '创建人', name: 'createPeople', width: 20}

        ],

        // viewrecords: true,
        // height: 385,
        // rowNum: 10,
        // rowList : [10,30,50],
        // rownumbers: true,
        // rownumWidth: 25,
        // autowidth:true,
        // multiselect: true,
		viewrecords: true,
        height: 385,
        rowNum: 10,
		rowList : [10,30,50],
        rownumbers: true, 
        rownumWidth: 25, 
        autowidth:true,
        multiselect: true,
        pager: "#jqGridPager",
        jsonReader : {
            root: "page.list",
            page: "page.currPage",
            total: "page.totalPage",
            records: "page.totalCount"
        },
        prmNames : {
            page:"page", 
            rows:"limit", 
            order: "order"
        },
        gridComplete:function(){
        	//隐藏grid底部滚动条
        	$("#jqGrid").closest(".ui-jqgrid-bdiv").css({ "overflow-x" : "hidden" }); 
        }
    });
});
var setting = {
    data: {
        simpleData: {
            enable: true,
            idKey: "deptId",
            pIdKey: "parentId",
            rootPId: -1
        },
        key: {
            url:"nourl"
        }
    }
};
var ztree;

var vm = new Vue({

    el:'#rrapp',
    data:{
        q:{
        	productName:null
        },
        showList: true,
        title:null,
        product:{
            productId:null,
            productName:null,
            productVersion:null,
            productContext:null,
            productCompany:null,
            productType:null,
        }
    },
    methods: {
        query: function () {
            vm.reload();
        },
        add: function(){
 
            vm.showList = false;
            vm.title = "新增";
            vm.product = {
                    productType:null,
                      deptId:null,
                      deptName:null,
            		  productId:null,
            		  productName:null,
            		  productCompany:null,
                      productContext:null,
                      productStatus:null
            };
            vm.getDept();
        },
        getDept: function(){
            //加载公司树
            $.get(baseURL + "sys/dept/list", function(r){
                ztree = $.fn.zTree.init($("#deptTree"), setting, r);
                var node = ztree.getNodeByParam("deptId", vm.product.deptId);
                if(node != null){
                    ztree.selectNode(node);
                    vm.product.productCompany = node.name;

                }
            })
        },
        update: function () {
            var modelId = getSelectedRow();

            if(modelId == null){
                return ;
            }
            vm.showList = false;
            vm.title = "修改";
            vm.getmodel(modelId);
           // var ue = UE.getEditor('editor');

            //获取角色信息
        },
        
        del: function () {
            var productId = getSelectedRows();
            console.log(productId);
            console.log('11111')

            if(productId == null){
                return ;
            }
            confirm('确定要删除选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/product/delete",
                    contentType: "application/json",
                    data: JSON.stringify(productId),
                    success: function(r){
                        if(r.code == 0){
                            alert('操作成功', function(){
                                location.href="modules/product/productlist.html";
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },

        devlopr: function () {
            var productId = getSelectedRows();


            if(productId == null){
                return ;
            }
            confirm('确定要上线选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/product/devlopr",
                    contentType: "application/json",
                    data: JSON.stringify(productId),
                    success: function(r){
                        if(r.code == 0){
                            alert('操作成功', function(){
                                location.href="modules/product/productlist.html";
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        low: function () {
            var productId = getSelectedRows();
            if(productId == null){
                return ;
            }
            confirm('确定要下线选中的记录？', function(){
                $.ajax({
                    type: "POST",
                    url: baseURL + "sys/product/low",
                    contentType: "application/json",
                    data: JSON.stringify(productId),
                    success: function(r){
                        if(r.code == 0){
                            alert('操作成功', function(){
                                location.href="modules/product/productlist.html";
                            });
                        }else{
                            alert(r.msg);
                        }
                    }
                });
            });
        },
        saveOrUpdate: function () {
            var url = vm.product.productId == null ? "sys/product/add" : "sys/product/update";
            $.ajax({
                type: "POST",
                url: baseURL + url,
                contentType: "application/json",
                data: JSON.stringify(vm.product),
                success: function(r){
                    if(r.code === 0){
                        alert('操作成功', function(){
                            location.href="modules/product/productlist.html";
                        });
                    }else{
                        alert(r.msg);
                    }
                }
            });
        },
        getmodel: function(productId){
            $.get(baseURL + "sys/product/info/"+productId, function(r){
                vm.product = r.product;
                vm.getDept();
            });
        },
        deptTree: function(){
            layer.open({
                type: 1,
                offset: '50px',
                skin: 'layui-layer-molv',
                title: "选择公司",
                area: ['300px', '450px'],
                shade: 0,
                shadeClose: false,
                content: jQuery("#deptLayer"),
                btn: ['确定', '取消'],
                btn1: function (index) {
                    var node = ztree.getSelectedNodes();
                    //选择上级公司
                    vm.product.deptId = node[0].deptId;
                    vm.product.productCompany = node[0].name;
                    layer.close(index);
                }
            });
        },
        reload: function () {
            vm.showList = true;
            var page = $("#jqGrid").jqGrid('getGridParam','page');
            $("#jqGrid").jqGrid('setGridParam',{
                postData:{'productName': vm.q.productName},
                page:page
            }).trigger("reloadGrid");
        }
    }
});