package org.yasukusury.onlinedocument.biz.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yasukusury.onlinedocument.biz.entity.Image;
import org.yasukusury.onlinedocument.biz.service.ImageService;
import org.yasukusury.onlinedocument.biz.service.FileService;
import org.yasukusury.onlinedocument.biz.service.UserService;
import org.yasukusury.onlinedocument.commons.base.BaseController;
import org.yasukusury.onlinedocument.commons.config.WebConf;
import org.yasukusury.onlinedocument.commons.json.MdResult;
import org.yasukusury.onlinedocument.commons.json.Result;
import org.yasukusury.onlinedocument.commons.utils.DateTool;

/**
 * @author 30254
 * creadtedate: 2019/1/4
 */
@RequestMapping("/upload")
@RestController
@CommonsLog
@Api("/upload")
public class UploadController extends BaseController {

    @Autowired
    private FileService fileService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @PostMapping({"/image/{type}","/image/{type}/{tid}"})
    @ApiOperation("上传书籍图片")
    public MdResult mdImage(@RequestParam("editormd-image-file") MultipartFile multipartFile
            , @PathVariable("type") String type, @PathVariable(value = "tid", required = false) Long tid) {
        MdResult result = new MdResult();
        try {
            if (getCUser() == null) {
                throw new Exception();
            }
            String format = multipartFile.getOriginalFilename();
            format = format.substring(format.lastIndexOf('.'));
            String fileName;
            switch (type){
                case ImageService.PORTRAIT:
                    fileName = WebConf.UPLOAD_PATH + "/images/" + DateTool.getTime().getTime() + format;
                    break;
                case ImageService.BOOK:
                    fileName = WebConf.UPLOAD_PATH + "/book/" + tid + "/images/" + DateTool.getTime().getTime() + format;
                    break;
                default:
                case ImageService.CHAPTER:
                    type = ImageService.CHAPTER;
                    fileName = WebConf.UPLOAD_PATH + "/book/" + tid + "/images/" + DateTool.getTime().getTime() + format;
                    break;
            }
            String url = fileService.image(multipartFile, fileName);
            Image image = Image.builder().url(url).tid(tid).area(type).build();
            imageService.save(image);
            if (type.equals(ImageService.PORTRAIT)){
                userService.updateUserPortrait(url, tid);
            }
            result.setUrl(url);
            result.setSuccess(1);
            result.setMsg("上传成功");
        } catch (Exception e) {
            result.setSuccess(0);
            result.setMsg("上传失败");
        }
        return result;
    }

    @PostMapping("/mdFile")
    public Result mdFile(@RequestParam("file") MultipartFile multipartFile) {
        Result result = new Result();
        try {
            result.setSuccess(true);
            result.setMsg("上传成功");
        } catch (Exception e) {
            result.setMsg("上传失败");
        }
        return result;
    }

}
