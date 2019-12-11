package com.test

import groovy.json.JsonSlurperClassic

class Settings implements Serializable{
    private Object _settings
    private String _branch
    private String _project
    Settings(String json, context){
        _settings = new JsonSlurperClassic().parseText(json)
        context.echo json
    }
    def getAt(String item){
        if(_project == null)
            throw new Exception("Settings error: Region is not set")
        if(_branch == null)
            throw new Exception("Settings error: Environment is not set")
        if(!_settings.containsKey(item))
            return ''
        context
        return _settings[_project][_branch][item]
    }
    def setProject(String project){
        _project = project
    }
    def setBranch(String branch){
        _branch = branch
    }
    def getRegions(){
        return _settings.keySet() as String[]
    }
    def containsRegion(String project){
        return _settings.containsKey(project)
    }
}