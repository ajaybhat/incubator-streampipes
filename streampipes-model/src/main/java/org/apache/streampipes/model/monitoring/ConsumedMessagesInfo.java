/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.apache.streampipes.model.monitoring;

import org.apache.streampipes.model.shared.annotation.TsModel;

@TsModel
public class ConsumedMessagesInfo extends MessagesInfo {

  private long totalMessagesSincePipelineStart;
  private long consumedMessagesSincePipelineStart;
  private long lag;

  public ConsumedMessagesInfo() {
  }

  public ConsumedMessagesInfo(String topicName, String groupId) {
    super(topicName, groupId);
  }

  public long getTotalMessagesSincePipelineStart() {
    return totalMessagesSincePipelineStart;
  }

  public void setTotalMessagesSincePipelineStart(long totalMessagesSincePipelineStart) {
    this.totalMessagesSincePipelineStart = totalMessagesSincePipelineStart;
  }

  public long getConsumedMessagesSincePipelineStart() {
    return consumedMessagesSincePipelineStart;
  }

  public void setConsumedMessagesSincePipelineStart(long consumedMessagesSincePipelineStart) {
    this.consumedMessagesSincePipelineStart = consumedMessagesSincePipelineStart;
  }

  public long getLag() {
    return lag;
  }

  public void setLag(long lag) {
    this.lag = lag;
  }

  public String getTopicName() {
    return topicName;
  }

  public void setTopicName(String topicName) {
    this.topicName = topicName;
  }
}
