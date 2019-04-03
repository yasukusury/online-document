<div class="left-nav" id="naviBar">
    <div id="logoBar" class="navbar-header">
        <div id="book-mini">
            <img class="book-cover" src="${book.cover!"/static/image/emp-book.png"}">
            <span style="margin-left: 15px"></span>
            <div>
                <div id="book-name">${book.name!} </div>
                <div id="book-visit" title="浏览" class="btn "><i
                        class="glyphicon glyphicon-eye-open"></i><span>${book.visit!"0"}</span></div>
                <div id="book-love" title="收藏" class="btn "><i class="layui-icon layui-icon-rate"></i><span>${book.love!"0"}</span>
                </div>
                <div id="book-praise" title="点赞" class="btn "><i
                        class="layui-icon layui-icon-praise"></i><span>${book.praise!"0"}</span>
                </div>
            </div>
        </div>
        <!--<a class="navbar-brand btn index" style="font-size: 30px; color: blue" href="/">Realm</a>-->
    </div>

    <div id="chapterPanel" class="doc-nav ">
        <div id="docTree" class="doc-section ztree full-height">

        </div>
    </div>
    <div id="commentPanel" class="doc-nav  mxhide">
        <div>
            <button id="commentBtn" class="btn btn-default" data-toggle="modal" data-target="commentModal"><i
                    class="glyphicon glyphicon-pencil" style="color: blue;"></i>留言本章节
            </button>
        </div>
        <div class="modal fade " id="commentModal" tabindex="-1" role="dialog">
            <div class="modal-dialog " role="document" aria-hidden="true" style="width: 650px">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span
                                aria-hidden="true">&times;</span></button>
                        <h3 class="modal-title">留言</h3>
                    </div>
                    <form id="commentForm" class="layui-form">
                        <div class="modal-body">
                            <textarea class="layui-textarea" name="content" maxlength="300" placeholder="请输入留言"></textarea>
                        </div>
                        <div class="modal-footer">
                            <div class="form-group">
                                <div class="col-sm-offset-9 col-sm-3">
                                    <button type="submit" class="btn btn-primary">发表</button>
                                    <button type="button" class="btn btn-default" data-dismiss="modal">取消</button>
                                </div>
                            </div>

                        </div>
                    </form>
                </div>
            </div>
        </div>
        <div id="commentBox" class="full-height">

        </div>
    </div>
</div>