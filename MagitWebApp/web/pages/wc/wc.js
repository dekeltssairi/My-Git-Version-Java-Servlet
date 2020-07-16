var DELETE_FILE_URL = buildUrlWithContextPath("deleteFile");
var CHANGE_FILE_URL = buildUrlWithContextPath("changeFile");
var ADD_FILE_URL = buildUrlWithContextPath("addFile");

var repository = undefined;

function showWC (repositoryFile) {

    var subFiles = repositoryFile.m_RepositoryFiles;
    var changeButton = document.getElementById("change_button");
    $("#change_button").unbind("click");
    if (changeButton.getAttribute("path") != null){
        changeButton.setAttribute("path", null);
    }
    $("#change_button").click(function () {
        var path = this.getAttribute("path");
        var content = document.getElementById('text_area').value;
        if (path != null && path != "null"){
            this.setAttribute("path", null);
            $.ajax({
                url: CHANGE_FILE_URL,
                data: {repository_name: repository.m_Name, path: path, content: content},
                success: function(repo) {
                    alert("seccessfully changed file content");
                    $("#files").empty();
                    // repository = JSON.parse(activeCommitMf);
                    // showWC(activeCommitMf);
                    repository = repo;
                    localStorage.setItem("repository", JSON.stringify(repo));
                    showWC(repository.m_WC.m_MainFolder);
                    showOpenChanges();
                    document.getElementById('text_area').value ="";
                },
                error: function(e){
                    console.log("error haaaaaaaaaaaaaa")
                }
            });
        }
        else {
            alert("Please choose first show content to be able to change the file.");
        }

    });


    var $delete=$('<input/>').attr({type:'button', name: repositoryFile.m_Sha_1, value: 'Delete'});
    $delete.click(function () {
        // ajax request for deleting
        $.ajax({
            url: DELETE_FILE_URL,
            data: {repositoryname: repository.m_Name, sha1: this.getAttribute("name")},
            success: function(repo) {
                console.log("in showCommit event");
                $("#files").empty();
                repository = repo;
                localStorage.setItem("repository", JSON.stringify(repo));
                showWC(repository.m_WC.m_MainFolder);
                showOpenChanges();
            },
            error: function(e){
                console.log("error haaaaaaaaaaaaaa")
            }
        });
        document.getElementById('text_area').value="";
    });

    if (subFiles !== undefined) {      // Folder
        $('<p>' + repositoryFile.m_Name + " " + repositoryFile.m_Path + " Folder" + '</p>').append($delete).appendTo($("#files"));
        var numOfSubFiles = subFiles.length;

        for (var i = 0; i < numOfSubFiles; i++) {
            showWC(subFiles[i]);
        }
    } else {                          // Blob
        var $show=$('<input/>').attr({type:'button', content: repositoryFile.m_Content, path: repositoryFile.m_Path, value: 'Show Content'});
        $show.click(function () {
            debugger;
            document.getElementById('text_area').value = this.getAttribute("content");
            document.getElementById('change_button').setAttribute("path", this.getAttribute("path"));
        });
        $('<p>' + repositoryFile.m_Name +  " " + repositoryFile.m_Path + " Blob" + '</p>').append($show).append($delete).appendTo($("#files"));
    }
}

function showOpenChanges() {
    $("#addedfiles").empty();
    $("#deletedfiles").empty();
    $("#changedfiles").empty();
    var added = repository.m_Delta.m_Added;
    var deleted = repository.m_Delta.m_Deleted;
    var changed = repository.m_Delta.m_Changed;

    var firstAdded = true;
    var firstDeleted = true;
    var firstChanged = true;
    for(var i = 0; i<added.length; i++){
        if(firstAdded){
            $("#addedTitle").text("Added files:");
            firstAdded = false;
        }
        $('<li>' + added[i].m_Name + '</li>').appendTo($("#addedfiles"));
    }
    for(var i = 0; i<deleted.length; i++){
        if(firstDeleted){
            $("#deletedTitle").text("Deleted files:");
            firstDeleted = false;
        }
        $('<li>' + deleted[i].m_Name + '</li>').appendTo($("#deletedfiles"));
    }
    for(var i = 0; i<changed.length; i++){
        if(firstChanged){
            $("#changedTitle").text("Changed files:");
            firstChanged = false;
        }
        $('<li>' + changed[i].m_Name + '</li>').appendTo($("#changedfiles"));
    }
}

$(function () {
    repository = JSON.parse(localStorage.getItem("repository"));
    document.getElementById('prefix').innerText = repository.m_Path + "\\";
    $("#back").click(function () {
        window.location.href = "../singleRepository/singleRepository.html";
    });

    $("#add").click(function () {
        debugger;
        var relativePath =   document.getElementById('add_input').value;
        var fullPath = document.getElementById('prefix').innerText + relativePath;
        var content = document.getElementById('input_text_area').value;
        $.ajax({
            url: ADD_FILE_URL,
            data: {repositoryname: repository.m_Name, path: fullPath, content: content},
            success: function(repo) {
                console.log("in showCommit event");
                $("#files").empty();
                repository = repo;
                localStorage.setItem("repository", JSON.stringify(repo));
                showWC(repository.m_WC.m_MainFolder);
                showOpenChanges();
                document.getElementById('add_input').value ="";
                document.getElementById('input_text_area').value="";
                document.getElementById("text_area").value = "";
            },
            error: function(e){
                console.log("error haaaaaaaaaaaaaa")
            }
        });
    });
    showWC(repository.m_WC.m_MainFolder);
    showOpenChanges();
});