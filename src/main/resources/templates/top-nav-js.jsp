<script type="text/javascript" src="/static/layui/layui.all.js"></script>
<script type="text/javascript" src="/static/js/myJs.js"></script>
<script type="text/javascript">
    var layer;
    layui.use('layer', function () {
        layer = layui.layer;

    });
    $(document).ready(function () {
        @@ if (login) { $$
            var username = $('#username');
            username.html('${cUser.username!}');
            $('#bookBtn').removeClass('mhide');
        @@ } else { $$
            var userMenu = $('#userMenu');
            userMenu.parent().on('shown.bs.dropdown', function () {
                userMenu.dropdown('toggle');
                layer.open({
                    title: '登录',
                    type: 2,
                    area: ['500px', '350px'],
                    content: '/login'
                });
            });
        @@ } $$
        $('a.post-a').on('click', function (e) {
            var action = e.currentTarget.href;
            $.post(action, function (data) {
                if (data.success) {
                    layer.msg(data.msg, {
                        icon: 1,
                        skin: 'layer-ext-moon',
                        time : 2000
                    }, function () {
                        layer.closeAll();
                        window.location.reload();
                    });
                } else {
                    layer.msg(data.msg, {
                        icon: 2,
                        skin: 'layer-ext-moon',
                        time : 2000
                    });
                }
            });
            return false;
        });
        $('.bookmark-a').on('click',function () {
            layer.open({
                title: '我的书签',
                type: 2,
                area: ['500px', '350px'],
                content: '/user/bookmark/'
            });
        });
        var table = jsonUrlParams();
        if (table.goLogin !== undefined){
            layer.open({
                title: '登录',
                type: 2,
                area: ['500px', '350px'],
                content: '/login'
            });
        }
    });
</script>