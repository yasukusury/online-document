<script>
    var commentListId;
    var bookmark;
    var act = {};
    act.love = [];
    act.book = [];
    act.comment = [];

    var tabContainer =
        '    <div class="layui-tab layui-tab-brief" style="display: flex;flex-direction: column; height: 100%">\n' +
        '        <ul class="layui-tab-title">\n' +
        '            <li class="layui-this">导航栏</li>\n' +
        '            <li id="commentTab">留言板</li>\n' +
        '        </ul>\n' +
        '        <div class="layui-tab-content" style="flex-grow: 2; overflow-y: auto;">\n' +
        '            <div id="tab1" class="layui-tab-item layui-show "></div>\n' +
        '            <div id="tab2" class="layui-tab-item"></div>\n' +
        '        </div>\n' +
        '    </div>';
    $('#chapterPanel').before(tabContainer);

    function createComment(comment) {
        var template =
            '<div class="comment-panel">' +
            '    <div>' +
            '        <span class="comment-user">' + comment.user + '</span>' +
            '        <span class="btn " style="position: absolute;right: 0">' +
            '            ' + new Date(comment.createDate).format("yyyy-MM-dd hh:mm") +
            '            <span class="comment-praise ' + (act.comment.indexOf(comment.id)>=0 ? 'has' : '') + '">' +
            '                <i class="layui-icon layui-icon-praise"></i>' +
            '                <span id="comment_' + comment.id + '">' + comment.praise + '</span>' +
            '            </span>' +
            '        </span>' +
            '    </div>' +
            '    <div class="comment-content">' + comment.content + '</div>' +
            '</div>';

        return template;
    }

    function createCommentList(list) {
        var l = [];
        for (var i in list) {
            l.push(createComment(list[i]));
        }
        l.reverse();
        return l;
    }

    function loadComment(id) {
        if (id !== commentListId) {
            commentListId = id;
            $.get('/comment/list/c' + id, function (data) {
                if (data.success) {
                    $('#commentBox').html(createCommentList(data.data).join(' '));
                }
            });
        }
    }

    function getBookmark() {
        $.get('/bookmark/get/b' + book.id, function (data) {
            if (data.success && data.data) {
                var percent = jsonUrlParams(data.data.url.split('?')[1]).progress;
                var text = '<span class="bookmark-text">' + data.data.chapter + '</span><span style="float: right">' + percent + '%</span>';
                $('#markText').html(text);
                bookmark = data.data;
                $('#goto').removeAttr('disabled')
            }
        });
    }

    function mark() {
        if (curId === 0) {
            return;
        }
        var doc = document.querySelector('#doc-cont');
        var percent = (doc.scrollTop/ doc.scrollHeight * 100).toFixed(1);
        var json = {progress: percent};
        $.post('/bookmark/mark/c'+curId, json, function (data) {
            if (data.success) {
                var text = '<span class="bookmark-text">' + data.data.chapter + '</span><span style="float: right">' + percent + '%</span>';
                $('#markText').html(text);
                bookmark = data.data;
                layer.msg(data.msg, {
                    icon: 1,
                    skin: 'layer-ext-moon',
                    time: 2000
                });
            } else {
                layer.msg(data.msg, {
                    icon: 2,
                    skin: 'layer-ext-moon',
                    time: 2000
                });
            }
        });
    }

    function go(params) {
        curId = params.nav;
        myZTree.selectNode(myZTree.getNodeByParam('id', curId));
        getText(curId, params.progress);

    }


    $(document).ready(function () {
        layui.use('form', function () {
           var form = layui.form;
            form.on('submit()', function (data) {
                var form = $("#commentForm");
                var formData = form.serialize();
                $.post('/comment/mark/b' + book.id + '/c' + curId, formData, function (data) {
                    if (data.success) {
                        layer.msg(data.msg, {
                            icon: 1,
                            skin: 'layer-ext-moon',
                            time: 2000
                        }, function () {
                            $('#commentModal').modal('hide');
                            var c = createComment(data.data);
                            $('#commentBox').prepend(c);
                        });
                    } else {
                        layer.msg(data.msg, {
                            icon: 2,
                            skin: 'layer-ext-moon',
                            time: 2000
                        });
                    }
                });
                return false; //阻止表单跳转。
            });
        });
        var loveDiv = $('#book-love');
        var loveIcon = $('#book-love>i');
        var loveText = $('#book-love>span');
        var praiseDiv = $('#book-praise');
        var praiseIcon = $('#book-praise>i');
        var praiseText = $('#book-praise>span');

        @@ if (login) { $$
            bookmark =
                '<div id="bookmark">\n' +
                '    <i class="glyphicon glyphicon-bookmark" style="color: indianred;"></i>' +
                '    <a id="mark" class="btn btn-default" >记录</a>' +
                '    <a id="goto" class="btn btn-default" disabled="true">跳转</a>' +
                '    <div id="markText"></div>\n' +
                '</div>';
            $('#book-mini>div').append(bookmark);
            var bookmarkDiv = $('#bookmark');

            bookmark = ${json(bookmark)};
            if (!$.isEmptyObject(bookmark)) {
                var percent = jsonUrlParams(bookmark.url.split('?')[1]).progress;
                var text = '<span class="bookmark-text">' + bookmark.chapter + '</span><span style="float: right">' + percent + '%</span>';
                $('#markText').html(text);
                $('#goto').removeAttr('disabled');
            }

            // getBookmark();

            $('#mark').on('click', function () {
                mark();
            });
            $('#goto').on('click', function () {
                go(jsonUrlParams(bookmark.url.split('?')[1]));
            });

            function toggleLove(flag){
                if (flag) {
                    loveDiv.addClass('has');
                    loveIcon.removeClass('layui-icon-rate');
                    loveIcon.addClass('layui-icon-rate-solid');
                } else {
                    loveDiv.removeClass('has');
                    loveIcon.removeClass('layui-icon-rate-solid');
                    loveIcon.addClass('layui-icon-rate');
                }
            }
            function togglePraise(flag){
                if (flag) {
                    praiseDiv.addClass('has');
                } else {
                    praiseDiv.removeClass('has');
                }
            }
            function toggleComment(id, comment, flag, text){
                if (flag) {
                    comment.addClass('has');
                    act.comment.push(id)
                } else {
                    comment.removeClass('has');
                    delete act.comment[act.comment.indexOf(id)];
                }
                comment.find('span').text(text);
            }
            // userAct
            var userAct = ${json(act)};

            var string2num = function (value) { return Number(value) };
            act.love = userAct.love.split(',').map(string2num);
            act.book = userAct.bookPraise.split(',').map(string2num);
            act.comment = userAct.commentPraise.split(',').map(string2num);
            if (act.love.indexOf(book.id) >= 0) {
                toggleLove(true);
            }
            if (act.book.indexOf(book.id) >= 0) {
                togglePraise(true);
            }
            // $.get('/user/act', function (data) {
            //     if (data.success) {
            //         var bookPraise = data.data.bookPraise.split(',');
            //         var love = data.data.love.split(',');
            //         if (love.indexOf('' + book.id) >= 0) {
            //             toggleLove(true);
            //         }
            //         if (bookPraise.indexOf('' + book.id) >= 0) {
            //             togglePraise(true);
            //         }
            //     }
            // });

        @@ } $$

        // love
        loveDiv.on('click', function () {
            var json = {};
            json.good = !loveDiv.hasClass('has');
            $.post('/book/love/b${book.id}', json, function (data) {
                if (data.success) {
                    toggleLove(!loveDiv.hasClass('has')) ;
                    loveText.text(data.data);
                    layer.msg(data.msg, {
                        icon: 1,
                        skin: 'layer-ext-moon',
                        time: 2000
                    });
                } else {
                    layer.msg(data.msg, {
                        icon: 2,
                        skin: 'layer-ext-moon',
                        time: 2000
                    });
                }
            });
        });
        // praise
        praiseDiv.on('click', function () {
            var json = {};
            json.good = !praiseDiv.hasClass('has');
            $.post('/book/praise/b${book.id}', json, function (data) {
                if (data.success) {
                    togglePraise(!praiseDiv.hasClass('has'));
                    praiseText.text(data.data);
                    layer.msg(data.msg, {
                        icon: 1,
                        skin: 'layer-ext-moon',
                        time: 2000
                    });
                } else {
                    layer.msg(data.msg, {
                        icon: 2,
                        skin: 'layer-ext-moon',
                        time: 2000
                    });
                }
            });
        });


        var commentPanel = $('#commentPanel');
        var chapterPanel = $('#chapterPanel');

        $('#commentTab').on('click', function () {
            loadComment(curId);
        });
        commentPanel.removeClass('mxhide');

        $('#tab1').append(chapterPanel);
        $('#tab2').append(commentPanel);

        $('body').append($('#commentModal'));
        $('#commentBtn').on('click', function () {
            if (myZTree.getNodeByParam('id', curId).hasText) {
                $('#commentModal').modal();
            }
        });

        $(document).on('click', '.comment-praise', function (e) {
            var json = {};
            var a = e.currentTarget;
            var id = a.querySelector('span').id.split('_')[1];
            a = $(a);
            json.good = !a.hasClass('has');
            $.post('/comment/praise/r' + id, json, function (data) {
                if (data.success) {
                    toggleComment(Number(id), a, !a.hasClass('has'), data.data);
                    layer.msg(data.msg, {
                        icon: 1,
                        skin: 'layer-ext-moon',
                        time: 2000
                    });
                } else {
                    layer.msg(data.msg, {
                        icon: 2,
                        skin: 'layer-ext-moon',
                        time: 2000
                    });
                }
            });

        });


    });
</script>