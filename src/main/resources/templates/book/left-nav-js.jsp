<script type="text/javascript">
    var zNodes, myZTree;
    var loading = false;
    var book = ${json(book)};
    var curId = 0;
    var treeObj = $("#docTree");
    var ztreeOptions = {
        edit: {
            drag: {
                autoExpandTrigger: true,
                isCopy: false
            },
            editNameSelectAll: true,
            enable: true,
            renameTitle: '重命名',
            removeTitle: '移除',
            showRemoveBtn: (editor ? banRoot : false),
            showRenameBtn: (editor ? banRoot : false)
        },
        view: {
            showLine: false,
            showIcon: false,
            selectedMulti: false,
            dblClickExpand: false,
            addHoverDom: (editor ? addHoverDom : null),
            removeHoverDom: (editor ? removeHoverDom : null),
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
            beforeDrag: banRoot,
            beforeDrop: beforeDrop,
            onDrop: onDrop,
            beforeRename: beforeRename,
            onRename: onRename,
            beforeRemove: beforeRemove,
            onRemove: onRemove,
            onDblClick: onDblClick
        }
    };

    var chapterDataMap = {};

    function getDocumentId(e) {
        var ele = e.currentTarget;
        return ele.getAttribute('data-id');
    }

    function initZTree() {
        function initChapterDataMap(obj) {
            for (var i = 0, l = obj.length; i < l; i++) {
                chapterDataMap[obj[i].id] = JSON.parse(JSON.stringify(obj[i]));
                chapterDataMap[obj[i].id].change = false;
            }
        }

        function warpChapterZTree(chapterList) {
            var cl = [];
            cl = $.extend(true, cl, chapterList);
            var nodes = [];
            for (var i = 0; i < cl.length; i++) {
                var node = {};
                node.id = cl[i].id;
                node.pid = cl[i].pid ? cl[i].pid : 0;
                node.name = cl[i].name;
                node.seq = cl[i].seq;
                node.hasText = !!cl[i].url;
                node.url = cl[i].url;
                nodes.push(node);
            }
            return nodes;
        }

        function cmp(a,b){
            return a.pid !== b.pid ? a.pid - b.pid : a.seq - b.seq;
        }

        zNodes = [{id: 0, name: book.name, pid: 'root'}];
        chapterDataMap = {};
        if (book.sub) {

            book.sub.sort(cmp);
            var nodes = warpChapterZTree(book.sub);
            zNodes = zNodes.concat(nodes);
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


    function hasText(id, md, html) {
        var json = {};
        json.md = md === undefined ? $('textarea[name=md]').val() : md;
        json.html = html === undefined ? $('textarea[name=editor-html-code]').val() : html;
        json.set = true;
        $.post('/chapter/text/b${book.id}/c' + id, json, function (data) {
            if (data.success) {
                loading = true;
                editor.setMarkdown(json.md);
                editor.codeEditor.clearHistory();
                myZTree.selectNode(myZTree.getNodeByParam('id', id));
                getText(id);
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

    function noText(id) {
        $.post('/chapter/text/b${book.id}/c' + id, {set: false}, function (data) {
            if (data.success) {
                loading = true;
                editor.setMarkdown('');
                editor.codeEditor.clearHistory();
                myZTree.selectNode(myZTree.getNodeByParam('id', id));
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

    function addDiyDom(treeId, treeNode) {
        var spaceWidth = 5;
        var switchObj = $("#" + treeNode.tId + "_switch"),
            icoObj = $("#" + treeNode.tId + "_ico");
        switchObj.remove();
        icoObj.before(switchObj);

        var spaceStr = "<span style='display: inline-block;width:" + (spaceWidth * treeNode.level) + "px'></span>";
        switchObj.before(spaceStr);
    }

    function beforeDrop(treeId, treeNodes, targetNode, moveType, isCopy) {
        function recurse(node) {
            var tId = node.tId;
            var lv = 'level' + node.level;
            var a = $('#' + tId + '_a');
            a.removeClass(lv);
            a.parent().removeClass(lv);
            $('#' + tId + '_switch').removeClass(lv);
            if (node.children) {
                $('#' + tId + '_ul').removeClass(lv);
                for (var j = 0, l = node.children.length; j < l; j++) {
                    recurse(node.children[j]);
                }
            }
        }

        function updateSub(node) {
            var p = node.getParentNode();
        }

        if (targetNode == null) return false;
        if (targetNode.pid == 'root' && (moveType == 'next' || moveType == 'prev')) return false;
        for (var i = 0, le = treeNodes.length; i < le; i++) {
            recurse(treeNodes[i]);
        }
    }

    function onDrop(event, treeId, treeNodes, targetNode, moveType, isCopy) {
        function recurse(len, node) {
            var tId = node.tId;
            var switchObj = $('#' + tId + '_switch');
            var space = switchObj.prev();
            var lv = 'level' + node.level;
            var a = $('#' + tId + '_a');
            a.addClass(lv);
            a.parent().addClass(lv);
            switchObj.addClass(lv);
            space.css('width', len + 'px');
            chapterDataMap[node.id].pid = node.getParentNode().id;
            chapterDataMap[node.id].change = true;
            if (node.children) {
                $('#' + tId + '_ul').addClass(lv);
                for (var i = 0, l = node.children.length; i < l; i++) {
                    recurse(len + 5, node.children[i]);
                }
            }
        }

        var width = 5 * treeNodes[0].level;
        for (var i = 0, le = treeNodes.length; i < le; i++) {
            recurse(width, treeNodes[i]);
        }
    }

    function beforeRename(treeId, treeNode, newName, isCancel) {
        if (treeNode.id < 0 && isCancel) {
            $.fn.zTree.getZTreeObj(treeId).removeNode(treeNode);
            return false;
        }
        if (newName.length == 0) {
            setTimeout(function () {
                var zTree = $.fn.zTree.getZTreeObj(treeId);
                zTree.cancelEditName();
                alert("节点名称不能为空.");
            }, 0);
            return false;
        }
        if (/[\/\\:*?"<>→]/g.test(newName)) {
            layer.alert('不能出现以下字符: \\ / : * ? " < > →', {
                icon: 1,
                skin: 'layer-ext-moon',
                time: 2000
            });
            return false;
        }

        return true;
    }

    function onRename(event, treeId, treeNode, isCancel) {
        chapterDataMap[treeNode.id].name = treeNode.name;
        chapterDataMap[treeNode.id].change = true;
    }

    function beforeRemove(treeId, treeNode) {
        if (treeNode.id < 0) return true;
        var zTree = $.fn.zTree.getZTreeObj(treeId);
        zTree.selectNode(treeNode);
        return confirm("确认删除 节点 -- " + treeNode.name + " 吗？\n连带的内容以及其下节点都会消失");
    }

    function onRemove(event, treeId, treeNode) {
        function recurse(node, time) {
            if (node.children) {
                for (var i = 0, l = node.children.length; i < l; i++) {
                    recurse(node.children, time);
                    if (node.id < 0) {
                        delete chapterDataMap[node.children[i].id];
                    } else {
                        chapterDataMap[node.children[i].id].deleteTime = time;
                        chapterDataMap[node.children[i].id].change = true;
                        chapterDataMap[node.children[i].id].valid = false;
                    }
                }
            }
        }

        var time = Date.now();
        chapterDataMap[treeNode.id].deleteTime = time;
        chapterDataMap[treeNode.id].change = true;
        chapterDataMap[treeNode.id].valid = false;
        recurse(treeNode, time);
        var p = chapterDataMap[treeNode.getParentNode().id];
        var l = p.sub.split(',');
        for (var x in l) {
            if (l[x] == treeNode.id) {
                l.splice(x, 1);
                break;
            }
        }
        chapterDataMap[p.id].sub = l.join(',');
        chapterDataMap[p.id].change = true;
    }

    function onDblClick(event, treeId, treeNode) {
        if (treeNode.hasText) {
            getText(treeNode.id);
        }
    }

    var newCount = -1;

    function addHoverDom(treeId, treeNode) {

        function onAdd() {
            var zTree = $.fn.zTree.getZTreeObj(treeId);
            var newNode = zTree.addNodes(treeNode, {
                id: (newCount),
                pid: treeNode.id,
                name: "new node" + (newCount--)
            });
            chapterDataMap[newNode[0].id] = {};
            chapterDataMap[newNode[0].id].id = newNode[0].id;
            chapterDataMap[newNode[0].id].pid = newNode[0].pid;
            chapterDataMap[newNode[0].id].name = newNode[0].name;
            chapterDataMap[newNode[0].id].url = null;
            chapterDataMap[newNode[0].id].valid = true;
            chapterDataMap[newNode[0].id].change = true;

            var l;
            if (chapterDataMap[treeNode.id].sub) {
                l = chapterDataMap[treeNode.id].sub.split(',');
            } else {
                l = [];
            }
            l.push(newNode[0].id);
            l.sort();
            chapterDataMap[treeNode.id].sub = l.join(',');
            chapterDataMap[treeNode.id].change = true;
            zTree.editName(newNode[0]);
            return false;
        }

        function onText() {
            var btn = $('#' + treeNode.tId + '_text');
            if (treeNode.hasText) {
                if (confirm("确认移除节点 -- " + treeNode.name + " 的页面吗？\n不可恢复")) {
                    treeNode.hasText = false;
                    btn.removeAttr('hasText');
                    noText(treeNode.id);
                    lockEditor(true);
                }
            } else {
                if (treeNode.id < 0) {
                    layer.msg('该节点尚未确实存在，请在保存节点信息后再尝试配置页面', {
                        icon: -1,
                        skin: 'layer-ext-moon',
                        time: 2000
                    });
                    onSave();
                    return;
                }
                treeNode.hasText = true;
                btn.attr('hasText', '');
                hasText(treeNode.id, '', '');
                lockEditor(false);
            }
            // saveChapter(json);
            return false;
        }

        function onSave() {
            function extractZtree() {
                var list = [];
                for (var x in chapterDataMap)
                    if (chapterDataMap.hasOwnProperty(x)){
                        var index = myZTree.getNodeByParam('id', x).getIndex();
                        if(chapterDataMap[x].change || chapterDataMap[x].seq != index)
                            chapterDataMap[x].seq = index;
                            list.push(chapterDataMap[x]);
                    }

                if (list.length == 0) return false;
                return {chapterList: JSON.stringify(list)};
            }

            function saveChapter(json) {
                function loadChapter() {
                    $.get('/book/b' + book.id, function (data) {
                        if (data.success) {
                            book = data.data;
                            myZTree.destroy();
                            initZTree();
                        }
                    });
                }
                $.post('/chapter/edit/b${book.id}', json, function (data) {
                    if (data.success) {
                        loadChapter();
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

            var json = extractZtree();
            if (json)
                saveChapter(json);
            return false;
        }

        var sObj = $("#" + treeNode.tId + "_span");
        if (treeNode.editNameFlag) return;
        var addStr, btn;
        if ($('#' + treeNode.tId + '_add').length <= 0) {
            addStr = '<span class="button add" id="' + treeNode.tId + '_add" title="增加子节点" onfocus="this.blur();"></span>';
            sObj.after(addStr);
            btn = $('#' + treeNode.tId + '_add');
            if (btn) btn.bind("click", onAdd);
        }
        if (banRoot(treeId, treeNode) && $('#' + treeNode.tId + '_text').length <= 0) {
            if (treeNode.hasText) {
                addStr = '<span class="button atta" id="' + treeNode.tId + '_text" title="移除页面" onfocus="this.blur();" hasText></span>';
            } else {
                addStr = '<span class="button atta" id="' + treeNode.tId + '_text" title="配置页面" onfocus="this.blur();" ></span>';
            }
            sObj.after(addStr);
            btn = $('#' + treeNode.tId + '_text');
            if (btn) btn.bind("click", onText);
        }
        if (!banRoot(treeId, treeNode) && $('#' + treeNode.tId + '_save').length <= 0) {
            addStr = '<span class="button save" id="' + treeNode.tId + '_save" title="保存全部节点" onfocus="this.blur();"></span>';
            sObj.after(addStr);
            btn = $('#' + treeNode.tId + '_save');
            if (btn) btn.bind("click", onSave);
        }
    }

    function removeHoverDom(treeId, treeNode) {
        if (!banRoot(treeId, treeNode)){
            return;
        }
        $('#' + treeNode.tId + '_add').unbind().remove();
        $('#' + treeNode.tId + '_text').unbind().remove();
    }

    function banRoot(treeId, treeNode) {
        return !(treeNode.pid == 'root');
    }

    $(document).ready(function () {
        initZTree();
        treeLoaded();
    })
</script>