package com.test

import groovy.json.JsonSlurperClassic

class Settings implements Serializable{
    private HashMap _settings
    private String _branch
    private String _project
    def _context
    Settings(String json, context){
        _settings = new JsonSlurperClassic().parseText(json)
        _context = context
    }
    String getAt(String item){
        _context.echo "Settings.getAt: ${item}"
        if(_project == null)
            throw new Exception("Settings error: Project name is not set")
        if(_branch == null)
            throw new Exception("Settings error: Branch name is not set")
        if(!_settings[_project][_branch].containsKey(item)){
            _context.echo "Settings.getAt: not contains ${item}"
            return ''
        }
        def result = _settings[_project][_branch][item] as String
        _context.echo "Settings.getAt: result is: ${result}"
        return result
    }
    def setProject(String project){
        _context.echo "Settings.setProject: ${project}"
        _project = project
    }
    def setBranch(String branch){
        _context.echo "Settings.setBranch: ${branch}"
        _branch = branch
    }
    String[] getProjects(){
        return _settings.keySet() as String[]
    }
    def containsProject(String project){
        return _settings.containsKey(project)
    }
}