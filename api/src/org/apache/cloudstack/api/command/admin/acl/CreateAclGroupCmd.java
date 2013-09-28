// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package org.apache.cloudstack.api.command.admin.acl;

import org.apache.log4j.Logger;

import org.apache.cloudstack.acl.AclGroup;
import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiCommandJobType;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.ApiErrorCode;
import org.apache.cloudstack.api.BaseAsyncCreateCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ServerApiException;
import org.apache.cloudstack.api.response.AclGroupResponse;
import org.apache.cloudstack.api.response.DomainResponse;

import com.cloud.event.EventTypes;
import com.cloud.exception.ResourceAllocationException;
import com.cloud.user.Account;

@APICommand(name = "createAclGroup", responseObject = AclGroupResponse.class, description = "Creates an acl group")
public class CreateAclGroupCmd extends BaseAsyncCreateCmd {
    public static final Logger s_logger = Logger.getLogger(CreateAclGroupCmd.class.getName());

    private static final String s_name = "createaclgroupresponse";

    // ///////////////////////////////////////////////////
    // ////////////// API parameters /////////////////////
    // ///////////////////////////////////////////////////

    @Parameter(name = ApiConstants.DOMAIN_ID, type = CommandType.UUID, description = "domainId of the account owning the acl group", entityType = DomainResponse.class)
    private Long domainId;

    @Parameter(name = ApiConstants.DESCRIPTION, type = CommandType.STRING, description = "optional description of the acl group")
    private String description;

    @Parameter(name = ApiConstants.NAME, type = CommandType.STRING, required = true, description = "name of the acl group")
    private String name;


    // ///////////////////////////////////////////////////
    // ///////////////// Accessors ///////////////////////
    // ///////////////////////////////////////////////////


    public String getDescription() {
        return description;
    }

    public Long getDomainId() {
        return domainId;
    }

    public String getName() {
        return name;
    }


    // ///////////////////////////////////////////////////
    // ///////////// API Implementation///////////////////
    // ///////////////////////////////////////////////////

    @Override
    public String getCommandName() {
        return s_name;
    }

    @Override
    public long getEntityOwnerId() {
        return Account.ACCOUNT_ID_SYSTEM;
    }

    @Override
    public void execute() {
        AclGroup role = _entityMgr.findById(AclGroup.class, getEntityId());
        if (role != null) {
            AclGroupResponse response = _responseGenerator.createAclGroupResponse(role);
            response.setResponseName(getCommandName());
            setResponseObject(response);
        } else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Failed to create acl group:" + name);
        }
    }

    @Override
    public void create() throws ResourceAllocationException {
        AclGroup result = _aclService.createAclGroup(domainId, name, description);
        if (result != null) {
            setEntityId(result.getId());
            setEntityUuid(result.getUuid());
        } else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Failed to create acl group entity" + name);
        }

    }

    @Override
    public String getEventType() {
        return EventTypes.EVENT_ACL_GROUP_CREATE;
    }

    @Override
    public String getEventDescription() {
        return "creating Acl group";
    }

    @Override
    public String getCreateEventType() {
        return EventTypes.EVENT_ACL_GROUP_CREATE;
    }

    @Override
    public String getCreateEventDescription() {
        return "creating acl group";
    }

    @Override
    public ApiCommandJobType getInstanceType() {
        return ApiCommandJobType.AclGroup;
    }

}
