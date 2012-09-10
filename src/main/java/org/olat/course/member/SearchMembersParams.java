/**
 * <a href="http://www.openolat.org">
 * OpenOLAT - Online Learning and Training</a><br>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); <br>
 * you may not use this file except in compliance with the License.<br>
 * You may obtain a copy of the License at the
 * <a href="http://www.apache.org/licenses/LICENSE-2.0">Apache homepage</a>
 * <p>
 * Unless required by applicable law or agreed to in writing,<br>
 * software distributed under the License is distributed on an "AS IS" BASIS, <br>
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. <br>
 * See the License for the specific language governing permissions and <br>
 * limitations under the License.
 * <p>
 * Initial code contributed and copyrighted by<br>
 * frentix GmbH, http://www.frentix.com
 * <p>
 */
package org.olat.course.member;

/**
 * 
 * @author srosse, stephane.rosse@frentix.com, http://www.frentix.com
 */
public class SearchMembersParams {
	private boolean repoOwners;
	private boolean repoTutors;
	private boolean repoParticipants;
	private boolean groupTutors;
	private boolean groupParticipants;
	private boolean groupWaitingList;
	
	public SearchMembersParams() {
		//
	}
	
	public SearchMembersParams(boolean repoOwners, boolean repoTutors, boolean repoParticipants,
			boolean groupTutors, boolean groupParticipants, boolean groupWaitingList) {
		this.repoOwners = repoOwners;
		this.repoTutors = repoTutors;
		this.repoParticipants = repoParticipants;
		this.groupTutors = groupTutors;
		this.groupParticipants = groupParticipants;
		this.groupWaitingList = groupWaitingList;
	}
	
	public boolean isRepoOwners() {
		return repoOwners;
	}
	
	public void setRepoOwners(boolean repoOwners) {
		this.repoOwners = repoOwners;
	}
	
	public boolean isRepoTutors() {
		return repoTutors;
	}
	
	public void setRepoTutors(boolean repoTutors) {
		this.repoTutors = repoTutors;
	}
	
	public boolean isRepoParticipants() {
		return repoParticipants;
	}
	
	public void setRepoParticipants(boolean repoParticipants) {
		this.repoParticipants = repoParticipants;
	}
	
	public boolean isGroupTutors() {
		return groupTutors;
	}
	
	public void setGroupTutors(boolean groupTutors) {
		this.groupTutors = groupTutors;
	}
	
	public boolean isGroupParticipants() {
		return groupParticipants;
	}
	
	public void setGroupParticipants(boolean groupParticipants) {
		this.groupParticipants = groupParticipants;
	}
	
	public boolean isGroupWaitingList() {
		return groupWaitingList;
	}
	
	public void setGroupWaitingList(boolean groupWaitingList) {
		this.groupWaitingList = groupWaitingList;
	}
}