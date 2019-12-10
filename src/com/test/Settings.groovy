package jobs.scripts

import groovy.json.JsonSlurperClassic

class Settings{
    private Object _settings
    private String _branch
    private String _project
    Settings(String json){
        _settings = new JsonSlurperClassic().parseText(json)
    }
    def getAt(String item){
        if(_project == null)
            throw new Exception("Settings error: Region is not set")
        if(_branch == null)
            throw new Exception("Settings error: Environment is not set")
        if(!_settings.containsKey(item))
            return ''
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