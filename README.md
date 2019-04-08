#online-document

#在线文档创作平台

The project is a app for the online-platform doing with document writing. 

- It offers a way to writing document in markdown and provides downloading of those docs completed in html.zip, pdf and docx.
- It contains some low-level user-interacting elements such as works reading, works reviewing, and keeps few users info like email-addr.
- It promotes the users within it in the way displaying some works of users, which might be in the list of well praised, most reviewed.   
- It makes schedule of doing a statistic about the tag of works. The hot tags will be displayed. 

The app is a stand-alone web app powered by SpringBoot, and other libs or plugins including elasticsearch, redis, and more as following:
### front-end

- **jquery** the famous javascript lib
- **bootstrap** a classical template lib
- **layui** another powerful template and framework lib
- **Editor.md** a plugin for markdown editor
- **ztree** a jq plugin for treeing ui
- **cropper** a nice plugin for image clipping
- **dropzone** a nice plugin for file uploading

### back-end

- **beetl** a powerful template engine better than jsp, freemarker, thymeleaf
- **redis** the well-known non-sql database
- **elasticsearch** the famous search engine

By the side, the app has token lots of time for implementing the document format converting. It can be mainly summarize that,
the earliest format md will be convert to html, than form html to pdf or docx, using the libs such as itextpdf, pdfbox, docx4j,
poi, flexmark  