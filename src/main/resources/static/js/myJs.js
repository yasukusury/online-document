

function createBook(book) {
    var template = '\n' +
        '                    <div class="book " data-id="' + book.id + '">\n' +
        '                        <a href="/book/document/b' + book.id + '" class="book-link book-cover" target="_blank">\n' +
        '                            <img src="' + book.cover + '" class="book-cover">\n' +
        '                        </a>\n' +
        '                        <div>\n' +
        '                            <a href="/book/document/b' + book.id + '" class="book-link" target="_blank"><span class="book-name">' + book.name + '</span></a>\n' +
        '                            <div>\n';
    var tags = book.tags.split(',');
    if (tags[0]) {
        template += '<div class="btn btn-default book-tag"><a href="/search/?keyword=' + tags[0] + '">' + tags[0] + '</a></div>';
        if (tags[1]) {
            template += '<div class="btn btn-default book-tag"><a href="/search/?keyword=' + tags[1] + '">' + tags[1] + '</a></div>';
            if (tags[2]) {
                template += '<div class="btn btn-default book-tag"><a href="/search/?keyword=' + tags[2] + '">' + tags[2] + '</a></div>';
            }
        }
    }
    template +=
        '                            </div>\n' +
        '                            <div class="book-author">By<a style="margin-left: 10px" href="/user/info/'+ book.authorId +'">' + book.author + '</a></div>\n' +
        '                            <div class="book-intro">' + book.introduction + '</div>\n' +
        '                            <div>' +
        '                                <div id="book-visit" title="浏览" class="btn "><i class="glyphicon glyphicon-eye-open"></i><span>' + (book.visit > 0 ? book.visit : 0) + '</span></div>\n' +
        '                                <div id="book-love" title="收藏" class="btn "><i class="layui-icon layui-icon-rate"></i><span>' + (book.love > 0 ? book.love : 0) + '</span></div>\n' +
        '                                <div id="book-praise" title="点赞" class="btn "><i class="layui-icon layui-icon-praise"></i><span>' + (book.praise > 0 ? book.praise : 0) + '</div>' +
        '                            </div>';
    template +=
        '                        </div>\n' +
        '                    </div>';
    return template;
}

function createBookList(bookList) {
    var content = [];
    for (var i in bookList) {
        var book = bookList[i];
        content.push(createBook(book));
    }
    return content;
}

function lockEditor(flag) {
    if (!editor) return;
    if (flag) {
        if (locked){
            return;
        }
        locked = true;
        var mainDiv = $('.doc-main');
        mainDiv.after('<div id="editorLock" class="full-height" style="left: 320px;right:0;z-index: 99;' +
            'background-color: black;opacity: 0.3;position: absolute;top: 0">' +
            '</div>');
        $('#editorLock').focus();
    } else {
        if (!locked){
            return;
        }
        locked = false;
        $('#editorLock').remove();
    }
}

function jsonUrlParams(params) {
    if (!params){
        params = location.search.split("?")[1];
    }
    var arr = decodeURIComponent(params).split("&");   //先通过？分解得到？后面的所需字符串，再将其通过&分解开存放在数组里
    var obj = {};
    for (var i in arr) {
        var pair = arr[i].split("=");
        obj[pair[0]] = pair[1];  //对数组每项用=分解开，=前为对象属性名，=后为属性值
    }
    return obj;
}

function encodeHtml(html) {
    var temp = document.createElement("div");
    (temp.textContent != null) ? (temp.textContent = html) : (temp.innerText = html);
    var output = temp.innerHTML;
    temp = null;
    return output;
}

function decodeHtml(text) {
    var temp = document.createElement("div");
    temp.innerHTML = text;
    var output = temp.innerText || temp.textContent;
    temp = null;
    return output;
}

Date.prototype.format = function(fmt)
{ //author: meizz
    var o = {
        "M+" : this.getMonth()+1,                 //月份
        "d+" : this.getDate(),                    //日
        "h+" : this.getHours(),                   //小时
        "m+" : this.getMinutes(),                 //分
        "s+" : this.getSeconds(),                 //秒
        "q+" : Math.floor((this.getMonth()+3)/3), //季度
        "S"  : this.getMilliseconds()             //毫秒
    };
    if(/(y+)/.test(fmt))
        fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
    for(var k in o)
        if(new RegExp("("+ k +")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length===1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
    return fmt;
}