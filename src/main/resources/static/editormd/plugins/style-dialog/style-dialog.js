(function () {
    var factory = function (exports) {
        var pluginName = "image-dialog";
        exports.fn.styleDialog = function () {
            var _this = this; // this == the current instance object of Editor.md
            var cm = this.cm;
            var lang = this.lang;
            var settings = this.settings;
            var editor = this.editor;
            var classPrefix = this.classPrefix;
            var dialogName = classPrefix + pluginName, dialog;
            cm.focus();

            if (editor.find("." + dialogName).length < 1) {
                var guid = (new Date).getTime();

                var dialogContent =
                    '<div class="' + classPrefix + 'form">\n' +
                    '    <label class="layui-form-label">工具栏样式</label>\n' +
                    '    <div class="layui-input-block"><select id="editormd-theme-select"></select></div>\n' +
                    '    <label class="layui-form-label">编辑器样式</label>' +
                    '    <div class="layui-input-block"><select id="editor-area-theme-select"></select></div>\n' +
                    '    <label class="layui-form-label">预览窗样式</label>' +
                    '    <div class="layui-input-block"><select  id="preview-area-theme-select"></select></div>\n' +
                    '</div>';

                dialog = this.createDialog({
                    title: '编辑器样式',
                    width: (settings.imageUpload) ? 465 : 380,
                    height: 254,
                    name: dialogName,
                    content: dialogContent,
                    mask: settings.dialogShowMask,
                    drag: settings.dialogDraggable,
                    lockScreen: settings.dialogLockScreen,
                    maskStyle: {
                        opacity: settings.dialogMaskOpacity,
                        backgroundColor: settings.dialogMaskBgColor
                    },
                    buttons: {
                        close: [lang.buttons.close, function () {
                        }]
                    }
                });

                dialog.attr("id", classPrefix + "style-dialog-" + guid);

                function themeSelect(id, themes, lsKey, callback) {
                    var select = $("#" + id);
                    for (var i = 0, len = themes.length; i < len; i++) {
                        var theme = themes[i];
                        var selected = (localStorage[lsKey] == theme) ? " selected=\"selected\"" : "";
                        select.append("<option value=\"" + theme + "\"" + selected + ">" + theme + "</option>");
                    }
                    select.bind("change", function () {
                        var theme = $(this).val();
                        if (theme === "") {
                            alert("theme == \"\"");
                            return false;
                        }
                        console.log("lsKey =>", lsKey, theme);
                        localStorage[lsKey] = theme;
                        callback(select, theme);
                    });
                    return select;
                }

                themeSelect("editormd-theme-select", editormd.themes, "theme", function ($this, theme) {
                    _this.setTheme(theme);
                });

                themeSelect("editor-area-theme-select", editormd.editorThemes, "editorTheme", function ($this, theme) {
                    _this.setCodeMirrorTheme(theme);
                });

                themeSelect("preview-area-theme-select", editormd.previewThemes, "previewTheme", function ($this, theme) {
                    _this.setPreviewTheme(theme);
                });
            }

            dialog = editor.find("." + dialogName);

            this.dialogShowMask(dialog);
            this.dialogLockScreen();
            dialog.show();
        };
    };

    // CommonJS/Node.js
    if (typeof require === "function" && typeof exports === "object" && typeof module === "object") {
        module.exports = factory;
    }
    else if (typeof define === "function")  // AMD/CMD/Sea.js
    {
        if (define.amd) { // for Require.js
            define(["editormd"], function (editormd) {
                factory(editormd);
            });
        } else { // for Sea.js
            define(function (require) {
                var editormd = require("./../../editormd");
                factory(editormd);
            });
        }
    }
    else {
        factory(window.editormd);
    }
})();