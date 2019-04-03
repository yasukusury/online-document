<div class="mtab-menu mpanel">
    <div class="mtab ${title == "info" ? "tab-selected" : ""} ">
        <a class="mtab" href="/user/info/${user.id}">简介</a>
    </div>
    <div class="mtab ${title == "bookList" ? "tab-selected" : ""} ">
        <a class="mtab" href="/user/bookList/${user.id}">文档</a>
    </div>
    <div class="mtab ${title == "collection" ? "tab-selected" : ""} ">
        <a class="mtab" href="/user/collection/${user.id}">收藏</a>
    </div>
</div>