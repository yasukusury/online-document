<div class="header-box">
    <nav class="navbar navbar-default navbar-fixed-top">
        <div class="container">
            <div class="navbar-header">
                <a class="navbar-brand btn index" style="font-size: 30px; color: blue" href="/">Realm</a>
            </div>
            <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
                <ul class="nav navbar-nav">
                    <li><a class="btn btn-default mnav-btn index" href="/">广场</a></li>
                    <li><a class="btn btn-default mnav-btn" href="/search/">发现</a></li>
                </ul>
                <ul class="nav navbar-nav navbar-right">
                    <li><a href="/search/" class="btn btn-default mnav-btn" >
                        <span class="glyphicon glyphicon-search"></span></a>
                        <!--
                        <div class="search-box mnav">
                            <input type="text" class="input" aria-describedby="basic-addon2">
                            <a type="button" class="btn btn-default mnav-btn">
                                <span class="glyphicon glyphicon-search" aria-hidden="true"></span>
                            </a>
                        </div>
                        -->
                    </li>
                    <li><a href="/user/bookList/${cUser.id!}" class="btn btn-default mnav-btn mhide" id="bookBtn">
                        <span class="glyphicon glyphicon-book" aria-hidden="true"></span></a>
                    </li>
                    <li class="dropdown">
                        <a class="btn btn-default dropdown-toggle mnav-btn" type="button" id="userMenu"
                           data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
                            <span id="username">未登录</span>
                            <span class="caret"></span>
                        </a>
                        <ul class="dropdown-menu dropdown-menu-right" aria-labelledby="dropdownMenu">
                            <li><a href="/user/info/${cUser.id!}">我的主页</a></li>
                            <li><a href="/user/bookList/${cUser.id!}">我的书籍</a></li>
                            <li><a class="bookmark-a">我的书签</a></li>
                            <li role="separator" class="divider"></li>
                            <li><a href="/logout" class="post-a">登出</a></li>
                        </ul>
                    </li>
                </ul>
            </div>
        </div>
    </nav>
</div>