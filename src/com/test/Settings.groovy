package com.test

import groovy.json.JsonSlurperClassic

class Settings implements Serializable
{
    private HashMap _settings
    private String _branch
    private String _project
    Settings(String json){
        _settings = new JsonSlurperClassic().parseText(json)
    }
    String getAt(String item)
    {
        if(_project == null)
            throw new Exception("Settings error: Project name is not set")
        if(_branch == null)
            throw new Exception("Settings error: Branch name is not set")
        if(!_settings[_project][_branch]?.containsKey(item))
        {
            return ''
        }
        def result = _settings[_project][_branch][item] as String
        return result
    }
    def setProject(String project)
    {
        _project = project
    }
    def setBranch(String branch)
    {
        _branch = branch
    }
    def getBranches(String project = '')
    {
        if(project == '' && _project.trim()){

            return _settings[_project].keySet() as String[]
        }
        return _settings[project].keySet() as String[]
    }
    String[] getProjects()
    {
        return _settings.keySet() as String[]
    }
    def containsProject(String project)
    {
        return _settings.containsKey(project)
    }
}