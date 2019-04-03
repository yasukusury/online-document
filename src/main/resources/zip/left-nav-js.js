
    var zNodes, myZTree;
    var loading = false;
    var curId = 0;
    var treeObj = $("#docTree");
    var ztreeOptions = {
        view: {
            showLine: false,
            showIcon: false,
            addDiyDom: addDiyDom
        },
        data: {
            simpleData: {
                enable: true,
                rootPId: 'root',
                pIdKey: 'pid'
            }
        },
        callback: {
            onDblClick: onDblClick
        }
    };

    var chapterDataMap = {};

    function warpChapterZTree(chapterList) {
        var cl = [];
        cl = $.extend(true, cl, chapterList);
        var nodes = [];
        for (var i = 0; i < cl.length; i++) {
            var node = {};
            node.id = cl[i].id;
            node.pid = cl[i].pid ? cl[i].pid : 0;
            node.name = cl[i].name;
            node.hasText = !!cl[i].url;
            node.url = cl[i].url;
            nodes.push(node);
        }
        return nodes;
    }

    function initChapterDataMap(obj) {
        for (var i = 0, l = obj.length; i < l; i++) {
            chapterDataMap[obj[i].id] = JSON.parse(JSON.stringify(obj[i]));
            chapterDataMap[obj[i].id].change = false;
        }
    }

    function initZTree() {
        zNodes = [{id: 0, name: book.name, pid: 'root'}];
        if (book.sub) {
            zNodes = zNodes.concat(warpChapterZTree(book.sub));
            // chapterDataMap[0] = {id: 0, name: book.name, pid: 'root', url: book.url, hasText: !!book.url};
            initChapterDataMap(book.sub);
        }

        myZTree = $.fn.zTree.init(treeObj, ztreeOptions, zNodes);

        treeObj.hover(function () {
            if (!treeObj.hasClass("showIcon")) {
                treeObj.addClass("showIcon");
            }
        }, function () {
            treeObj.removeClass("showIcon");
        });
        myZTree.expandNode(myZTree.getNodes()[0], true, false, false);
        myZTree.selectNode(myZTree.getNodes()[0]);
    }


    function addDiyDom(treeId, treeNode) {
        var spaceWidth = 5;
        var switchObj = $("#" + treeNode.tId + "_switch"),
            icoObj = $("#" + treeNode.tId + "_ico");
        switchObj.remove();
        icoObj.before(switchObj);

        var spaceStr = "<span style='display: inline-block;width:" + (spaceWidth * treeNode.level) + "px'></span>";
        switchObj.before(spaceStr);
    }

    function onDblClick(event, treeId, treeNode) {
        if (treeNode.hasText) {
            getText(treeNode.id);
        }
    }

    $(document).ready(function () {

        initZTree();
        treeLoaded();
    });
