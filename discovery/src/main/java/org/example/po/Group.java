package org.example.po;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author 10532
 */
@Data
public class Group implements Serializable {

    private static final long serialVersionUID = 1213897974544121L;

    private String group;

    private List<Server> server;

}
