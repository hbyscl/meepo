<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>meepo-任务管理</title>
    <link rel="stylesheet" href="plugin/layui/css/layui.css">
    <link rel="stylesheet" href="plugin/pace/pace.top.css">

</head>
<body>
<div class="layui-layout layui-layout-admin">
    <div class="layui-header">
        <div class="layui-logo">meepo-任务管理</div>
        <!-- 头部区域（可配合layui已有的水平导航） -->
        <ul class="layui-nav layui-layout-left">
        </ul>
        <ul class="layui-nav layui-layout-right">
            <li class="layui-nav-item">
                <a href="javascript:;" id="userName">
                ${userName!}
                </a>
            </li>
            <li class="layui-nav-item"><a id="logout" href="#">退出</a></li>
        </ul>
    </div>

    <div class="layui-side layui-bg-black">
        <div class="layui-side-scroll">
            <!-- 左侧导航区域（可配合layui已有的垂直导航） -->
            <ul class="layui-nav layui-nav-tree"  lay-filter="test">
                <li class="layui-nav-item"><a show="task">Task</a></li>
                <li class="layui-nav-item"><a show="scheduler">Scheduler</a></li>
            </ul>
        </div>
    </div>

    <div class="layui-body">
        <!-- 内容主体区域 -->
        <div style="padding: 15px;">内容主体区域</div>
    </div>

    <!--<div class="layui-footer">-->
    <!--&lt;!&ndash; 底部固定区域 &ndash;&gt;-->
    <!--© www.vteamsystem.com-->
    <!--</div>-->
</div>
</body>
<script type="text/javascript" src="plugin/jquery/jquery-2.2.3.min.js"></script>
<script type="text/javascript" src="plugin/pace/pace.min.js"></script>
<script type="text/javascript" src="plugin/layui/layui.all.js"></script>
<script type="text/javascript">
    !function(){
        $(document).ajaxStart(function () {
            Pace.restart();
        });
        var nav = $(".layui-nav-tree a");
        nav.on("click",function(a){
            var href = $(this).attr("show");
            $.get("page/"+href,function(data){
                $(".layui-body").html(data);
            });

        });
        $(nav[0]).click();

    }();
</script>
</html>