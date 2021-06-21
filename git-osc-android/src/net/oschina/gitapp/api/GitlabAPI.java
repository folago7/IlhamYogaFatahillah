package net.oschina.gitapp.api;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;

import net.oschina.gitapp.AppException;
import net.oschina.gitapp.bean.GitlabBranch;
import net.oschina.gitapp.bean.GitlabCommit;
import net.oschina.gitapp.bean.GitlabIssue;
import net.oschina.gitapp.bean.GitlabMergeRequest;
import net.oschina.gitapp.bean.GitlabMilestone;
import net.oschina.gitapp.bean.GitlabNamespace;
import net.oschina.gitapp.bean.GitlabNote;
import net.oschina.gitapp.bean.GitlabProject;
import net.oschina.gitapp.bean.GitlabProjectHook;
import net.oschina.gitapp.bean.GitlabProjectMember;
import net.oschina.gitapp.bean.GitlabSession;

/**
 * Gitlab API Wrapper class
 *
 * @author @timols
 */
public class GitlabAPI {
    private String _hostUrl = "http://git.oschina.net";
    private final String _apiToken;
    private boolean _ignoreCertificateErrors = false;
    private static final String API_NAMESPACE = "/api/v3";
    public static final ObjectMapper MAPPER = new ObjectMapper().configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private GitlabAPI(String apiToken) {
        _hostUrl = _hostUrl.endsWith("/") ? _hostUrl.replaceAll("/$", "") : _hostUrl;
        _apiToken = apiToken;
    }
    
    public static GitlabSession connect(String username, String password) throws AppException {
    	String tailUrl = GitlabSession.URL;
    	GitlabAPI api = connect(null);
    	return api.dispatch().with("email", username).with("password", password)
    			.to(tailUrl, GitlabSession.class);
    }

    public static GitlabAPI connect(String apiToken) {
        return new GitlabAPI(apiToken);
    }

    public GitlabAPI ignoreCertificateErrors(boolean ignoreCertificateErrors) {
        _ignoreCertificateErrors = ignoreCertificateErrors;
        return this;
    }

    public GitlabHTTPRequestor retrieve() {
        return new GitlabHTTPRequestor(this);
    }

    public GitlabHTTPRequestor dispatch() {
        return new GitlabHTTPRequestor(this).method("POST");
    }

    public boolean isIgnoreCertificateErrors() {
        return _ignoreCertificateErrors;
    }

    public URL getAPIUrl(String tailAPIUrl) throws IOException {
        if (_apiToken != null) {
            tailAPIUrl = tailAPIUrl + (tailAPIUrl.indexOf('?') > 0 ? '&' : '?') + "private_token=" + _apiToken;
        }

        if (!tailAPIUrl.startsWith("/")) {
            tailAPIUrl = "/" + tailAPIUrl;
        }
        return new URL(_hostUrl + API_NAMESPACE + tailAPIUrl);
    }

    public URL getUrl(String tailAPIUrl) throws IOException {
        if (!tailAPIUrl.startsWith("/")) {
            tailAPIUrl = "/" + tailAPIUrl;
        }

        return new URL(_hostUrl + tailAPIUrl);
    }

    public GitlabProject getProject(Integer projectId) throws AppException {
        String tailUrl = GitlabProject.URL + "/" + projectId;
        return retrieve().to(tailUrl, GitlabProject.class);
    }

    public List<GitlabProject> getProjects() throws AppException {
        String tailUrl = GitlabProject.URL;
        return retrieve().getAll(tailUrl, GitlabProject[].class);
    }

    public List<GitlabProject> getAllProjects() throws AppException {
        String tailUrl = GitlabProject.URL;
        return retrieve().getAll(tailUrl, GitlabProject[].class);
    }

    public List<GitlabMergeRequest> getOpenMergeRequests(GitlabProject project) throws AppException {
        List<GitlabMergeRequest> allMergeRequests = getAllMergeRequests(project);
        List<GitlabMergeRequest> openMergeRequests = new ArrayList<GitlabMergeRequest>();

        for (GitlabMergeRequest mergeRequest : allMergeRequests) {
            if (mergeRequest.isMerged() || mergeRequest.isClosed()) {
                continue;
            }
            openMergeRequests.add(mergeRequest);
        }

        return openMergeRequests;
    }

    public List<GitlabMergeRequest> getMergeRequests(Integer projectId) throws AppException {
        String tailUrl = GitlabProject.URL + "/" + projectId + GitlabMergeRequest.URL;
        return fetchMergeRequests(tailUrl);
    }

    public List<GitlabMergeRequest> getMergeRequests(GitlabProject project) throws AppException {
        String tailUrl = GitlabProject.URL + "/" + project.getId() + GitlabMergeRequest.URL;
        return fetchMergeRequests(tailUrl);
    }

    public List<GitlabMergeRequest> getAllMergeRequests(GitlabProject project) throws AppException {
        String tailUrl = GitlabProject.URL + "/" + project.getId() + GitlabMergeRequest.URL;
        return retrieve().getAll(tailUrl, GitlabMergeRequest[].class);
    }

    public GitlabMergeRequest getMergeRequest(GitlabProject project, Integer mergeRequestId) throws AppException{
        String tailUrl = GitlabProject.URL + "/" + project.getId() + "/merge_request/" + mergeRequestId;
        return retrieve().to(tailUrl, GitlabMergeRequest.class);
    }

    public List<GitlabNote> getNotes(GitlabMergeRequest mergeRequest) throws AppException {
        String tailUrl = GitlabProject.URL + "/" + mergeRequest.getProjectId() +
                GitlabMergeRequest.URL + "/" + mergeRequest.getId() +
                GitlabNote.URL;

        GitlabNote[] notes = retrieve().to(tailUrl, GitlabNote[].class);
        return Arrays.asList(notes);
    }

    public List<GitlabNote> getAllNotes(GitlabMergeRequest mergeRequest) throws AppException {
        String tailUrl = GitlabProject.URL + "/" + mergeRequest.getProjectId() +
                GitlabMergeRequest.URL + "/" + mergeRequest.getId() +
                GitlabNote.URL;
        
        return retrieve().getAll(tailUrl, GitlabNote[].class);

    }

    public List<GitlabCommit> getCommits(GitlabMergeRequest mergeRequest) throws AppException {
        Integer projectId = mergeRequest.getSourceProjectId();
        if (projectId == null) {
            projectId = mergeRequest.getProjectId();
        }
        String tailUrl = GitlabProject.URL + "/" + projectId +
                "/repository" + GitlabCommit.URL + "?ref_name=" + mergeRequest.getSourceBranch();

        GitlabCommit[] commits = retrieve().to(tailUrl, GitlabCommit[].class);
        return Arrays.asList(commits);
    }

    public GitlabNote createNote(GitlabMergeRequest mergeRequest, String body) throws AppException {
        String tailUrl = GitlabProject.URL + "/" + mergeRequest.getProjectId() +
                GitlabMergeRequest.URL + "/" + mergeRequest.getId() + GitlabNote.URL;

        return dispatch().with("body", body).to(tailUrl, GitlabNote.class);
    }
    
    public List<GitlabBranch> getBranches(GitlabProject project) throws Exception {
    	String tailUrl = GitlabProject.URL + "/" + project.getId() + GitlabBranch.URL;
        GitlabBranch[] branches = retrieve().to(tailUrl, GitlabBranch[].class);
        return Arrays.asList(branches);
    }
    
    public GitlabBranch getBranch(GitlabProject project, String branchName) throws AppException {
    	String tailUrl = GitlabProject.URL + "/" + project.getId() + GitlabBranch.URL + branchName;
        GitlabBranch branch = retrieve().to(tailUrl, GitlabBranch.class);
        return branch;
    }
    
    public void protectBranch(GitlabProject project, String branchName) throws AppException {
        String tailUrl = GitlabProject.URL + "/" + project.getId() + GitlabBranch.URL + branchName + "/protect";
        retrieve().method("PUT").to(tailUrl, Void.class);
    }
    
    public void unprotectBranch(GitlabProject project, String branchName) throws AppException {
        String tailUrl = GitlabProject.URL + "/" + project.getId() + GitlabBranch.URL + branchName + "/unprotect";
        retrieve().method("PUT").to(tailUrl, Void.class);
    }
    
    public List<GitlabProjectHook> getProjectHooks(GitlabProject project) throws AppException {
    	String tailUrl = GitlabProject.URL + "/" + project.getId() + GitlabProjectHook.URL;
    	GitlabProjectHook[] hooks = retrieve().to(tailUrl, GitlabProjectHook[].class);
        return Arrays.asList(hooks);
    }
    
    public GitlabProjectHook getProjectHook(GitlabProject project, String hookId) throws AppException {
    	String tailUrl = GitlabProject.URL + "/" + project.getId() + GitlabProjectHook.URL + "/" + hookId;
    	GitlabProjectHook hook = retrieve().to(tailUrl, GitlabProjectHook.class);
        return hook;
    }
    
    public GitlabProjectHook addProjectHook(GitlabProject project, String url) throws AppException {
    	String tailUrl = null;
		try {
			tailUrl = GitlabProject.URL + "/" + project.getId() + GitlabProjectHook.URL + "?url=" + URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return dispatch().to(tailUrl, GitlabProjectHook.class);
    }
    
    public GitlabProjectHook editProjectHook(GitlabProject project, String hookId, String url) throws AppException {
    	String tailUrl = null;
		try {
			tailUrl = GitlabProject.URL + "/" + project.getId() + GitlabProjectHook.URL + "/" + hookId + "?url=" + URLEncoder.encode(url, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
    	return retrieve().method("PUT").to(tailUrl, GitlabProjectHook.class);
    }
    
    public void deleteProjectHook(GitlabProject project, String hookId) throws AppException {
    	String tailUrl = GitlabProject.URL + "/" + project.getId() + GitlabProjectHook.URL + "/" + hookId;
    	retrieve().method("DELETE").to(tailUrl, Void.class);
    }

    private List<GitlabMergeRequest> fetchMergeRequests(String tailUrl) throws AppException {
        GitlabMergeRequest[] mergeRequests = retrieve().to(tailUrl, GitlabMergeRequest[].class);
        return Arrays.asList(mergeRequests);
    }
    
    public List<GitlabIssue> getIssues(GitlabProject project) throws AppException {
    	String tailUrl = GitlabProject.URL + "/" + project.getId() + GitlabIssue.URL;
    	return retrieve().getAll(tailUrl, GitlabIssue[].class);
    }
    
    public GitlabIssue getIssue(Integer projectId, Integer issueId) throws AppException {
    	String tailUrl = GitlabProject.URL + "/" + projectId + GitlabIssue.URL + "/" + issueId;
    	return retrieve().to(tailUrl, GitlabIssue.class);
    }
    
    public GitlabIssue createIssue(int projectId, int assigneeId, int milestoneId, String labels, 
    		String description, String title) throws AppException {
    	String tailUrl = GitlabProject.URL + "/" + projectId + GitlabIssue.URL;
    	GitlabHTTPRequestor requestor = dispatch();
    	applyIssue(requestor, projectId, assigneeId, milestoneId, labels, description, title);

    	return requestor.to(tailUrl, GitlabIssue.class);
    }
    
    public GitlabIssue editIssue(int projectId, int issueId, int assigneeId, int milestoneId, String labels,
    		String description, String title, GitlabIssue.Action action) throws AppException {
    	String tailUrl = GitlabProject.URL + "/" + projectId + GitlabIssue.URL + "/" + issueId;
    	GitlabHTTPRequestor requestor = retrieve().method("PUT");
    	applyIssue(requestor, projectId, assigneeId, milestoneId, labels, description, title);
    	
    	if(action != GitlabIssue.Action.LEAVE) {
    			requestor.with("state_event", action.toString().toLowerCase());
    	}
    	
    	return requestor.to(tailUrl, GitlabIssue.class);
    }
    
	private void applyIssue(GitlabHTTPRequestor requestor, int projectId,
			int assigneeId, int milestoneId, String labels, String description,
			String title) {
		
		requestor.with("title", title)
				.with("description", description)
				.with("labels", labels)
				.with("milestone_id", milestoneId);
		
		if(assigneeId != 0) {
			requestor.with("assignee_id", assigneeId == -1 ? 0 : assigneeId);
		}
	}
    
    public List<GitlabNote> getNotes(GitlabIssue issue) throws AppException {
    	String tailUrl = GitlabProject.URL + "/" + issue.getProjectId() + GitlabIssue.URL + "/" 
    			+ issue.getId() + GitlabNote.URL;
    	return Arrays.asList(retrieve().to(tailUrl, GitlabNote[].class));
    }
    
    public GitlabNote createNote(Integer projectId, Integer issueId, String message) throws AppException {
    	String tailUrl = GitlabProject.URL + "/" + projectId + GitlabIssue.URL
    			+ "/" + issueId + GitlabNote.URL;
    	return dispatch().with("body", message).to(tailUrl, GitlabNote.class);
    }
    
    public GitlabNote createNote(GitlabIssue issue, String message) throws AppException {
    	return createNote(issue.getProjectId(), issue.getId(), message);
    }
    
    public List<GitlabMilestone> getMilestones(GitlabProject project) throws AppException {
    	return getMilestones(project.getId());
    }
    
    public List<GitlabMilestone> getMilestones(Integer projectId) throws AppException {
    	String tailUrl = GitlabProject.URL + "/" + projectId + GitlabMilestone.URL;
    	return Arrays.asList(retrieve().to(tailUrl, GitlabMilestone[].class));
    }
    
    public List<GitlabProjectMember> getProjectMembers(GitlabProject project) throws AppException {
    	return getProjectMembers(project.getId());
    }
    
    public List<GitlabProjectMember> getProjectMembers(Integer projectId) throws AppException {
    	String tailUrl = GitlabProject.URL + "/" + projectId + GitlabProjectMember.URL;
    	return Arrays.asList(retrieve().to(tailUrl, GitlabProjectMember[].class));
    }
    
    /**
     * This will fail, if the given namespace is a user and not a group
     * @param namespace
     * @return
     * @throws AppException
     * @throws AppException 
     */
    public List<GitlabProjectMember> getNamespaceMembers(GitlabNamespace namespace) throws AppException {
    	return getNamespaceMembers(namespace.getId());
    }
    
    /**
     * This will fail, if the given namespace is a user and not a group
     * @param namespaceId
     * @return
     * @throws AppException
     * @throws AppException 
     */
    public List<GitlabProjectMember> getNamespaceMembers(Integer namespaceId) throws AppException {
    	String tailUrl = GitlabNamespace.URL + "/" + namespaceId + GitlabProjectMember.URL;
    	return Arrays.asList(retrieve().to(tailUrl, GitlabProjectMember[].class));
    }
    
    public GitlabSession getCurrentSession() throws Exception {
    	String tailUrl = "/user";
    	return retrieve().to(tailUrl, GitlabSession.class);
    }
}
