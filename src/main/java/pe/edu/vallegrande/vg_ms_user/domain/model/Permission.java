package pe.edu.vallegrande.vg_ms_user.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Map;

@Document(collection = "permissions")
public class Permission {
    @Id
    private String id;
    private String viewId;
    private Map<String, Map<String, Boolean>> permissions;

    public Permission() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getViewId() {
        return viewId;
    }

    public void setViewId(String viewId) {
        this.viewId = viewId;
    }

    public Map<String, Map<String, Boolean>> getPermissions() {
        return permissions;
    }

    public void setPermissions(Map<String, Map<String, Boolean>> permissions) {
        this.permissions = permissions;
    }
} 