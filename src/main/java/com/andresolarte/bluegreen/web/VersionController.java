package com.andresolarte.bluegreen.web;

import com.andresolarte.bluegreen.model.Versions;
import com.andresolarte.bluegreen.service.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/version")
public class VersionController {

    @Autowired
    private VersionService versionService;

    @RequestMapping(method = RequestMethod.GET,  produces = MediaType.APPLICATION_JSON_VALUE )
    @ResponseBody
    public Versions getVersions() {
        return versionService.getVersions();
    }

    @RequestMapping(method = RequestMethod.POST,  produces = MediaType.APPLICATION_JSON_VALUE )
    @ResponseBody
    public Versions postVersions(@RequestHeader(value="Blue") int blue,@RequestHeader(value="Green") int green ) {
        Versions versions = new Versions(blue, green);
        versionService.updateVersion(versions);
        return versions;
    }
}
