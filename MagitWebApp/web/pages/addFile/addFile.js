var ADD_FILE_URL = buildUrlWithContextPath("deleteFile");
$(function () {
    repository = JSON.parse(localStorage.getItem("repository"));
    shaFileName = localStorage.getItem("sha1");
    localStorage.clear();
    var typeOfFileToAdd = $( "#type option:selected" ).text();


    //showWC(repository.m_WC.m_MainFolder);
});