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

import {Component, Input, OnInit} from "@angular/core";
import {DialogRef} from "../../../core-ui/dialog/base-dialog/dialog-ref";
import {Message, Pipeline, PipelineCanvasMetadata} from "../../../core-model/gen/streampipes-model";
import {ObjectProvider} from "../../services/object-provider.service";
import {EditorService} from "../../services/editor.service";
import {PipelineService} from "../../../platform-services/apis/pipeline.service";
import {ShepherdService} from "../../../services/tour/shepherd.service";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Router} from "@angular/router";
import {PipelineCanvasMetadataService} from "../../../platform-services/apis/pipeline-canvas-metadata.service";

@Component({
  selector: 'save-pipeline',
  templateUrl: './save-pipeline.component.html',
  styleUrls: ['./save-pipeline.component.scss']
})
export class SavePipelineComponent implements OnInit {

  pipelineCategories: any;
  startPipelineAfterStorage: any;
  updateMode: any;

  submitPipelineForm: FormGroup = new FormGroup({});

  @Input()
  pipeline: Pipeline;

  @Input()
  modificationMode: string;

  @Input()
  currentModifiedPipelineId: string;

  @Input()
  pipelineCanvasMetadata: PipelineCanvasMetadata;

  saving: boolean = false;
  saved: boolean = false;

  storageError: boolean = false;
  errorMessage: string = '';

  currentPipelineName: string;

  constructor(private editorService: EditorService,
              private dialogRef: DialogRef<SavePipelineComponent>,
              private objectProvider: ObjectProvider,
              private pipelineService: PipelineService,
              private Router: Router,
              private ShepherdService: ShepherdService,
              private pipelineCanvasService: PipelineCanvasMetadataService) {
    this.pipelineCategories = [];
    this.updateMode = "update";
  }

  ngOnInit() {
    this.getPipelineCategories();
    if (this.currentModifiedPipelineId) {
      this.currentPipelineName = this.pipeline.name;
    }

    this.submitPipelineForm.addControl("pipelineName", new FormControl(this.pipeline.name,
        [Validators.required,
          Validators.maxLength(40)]))
    this.submitPipelineForm.addControl("pipelineDescription", new FormControl(this.pipeline.description,
        [Validators.maxLength(80)]))

    this.submitPipelineForm.controls["pipelineName"].valueChanges.subscribe(value => {
      this.pipeline.name = value;
    });

    this.submitPipelineForm.controls["pipelineDescription"].valueChanges.subscribe(value => {
      this.pipeline.description = value;
    });

    if (this.ShepherdService.isTourActive()) {
      this.ShepherdService.trigger("enter-pipeline-name");
    }

  }

  triggerTutorial() {
    if (this.ShepherdService.isTourActive()) {
      this.ShepherdService.trigger("save-pipeline-dialog");
    }
  }

  displayErrors(data?: string) {
    this.storageError = true;
    this.errorMessage = data;
  }

  getPipelineCategories() {
    this.pipelineService.getPipelineCategories().subscribe(pipelineCategories => {
      this.pipelineCategories = pipelineCategories;
    });
  };


  savePipeline(switchTab) {
    let storageRequest;
    let updateMode = this.currentModifiedPipelineId && this.updateMode === 'update';

    if (updateMode) {
      storageRequest = this.pipelineService.updatePipeline(this.pipeline);
    } else {
      this.pipeline._id = undefined;
      storageRequest = this.pipelineService.storePipeline(this.pipeline);
    }

    storageRequest
        .subscribe(statusMessage => {
          if (statusMessage.success) {
            let pipelineId: string = statusMessage.notifications[1].description;
            this.storePipelineCanvasMetadata(pipelineId, updateMode);
            this.afterStorage(statusMessage, switchTab, pipelineId);
          } else {
            this.displayErrors(statusMessage.notifications[0]);
          }
        }, data => {
          this.displayErrors();
        });
  };

  storePipelineCanvasMetadata(pipelineId: string,
                              updateMode: boolean) {
    let request;
    this.pipelineCanvasMetadata.pipelineId = pipelineId;
    if (updateMode) {
      request = this.pipelineCanvasService.updatePipelineCanvasMetadata(this.pipelineCanvasMetadata);
    } else {
      this.pipelineCanvasMetadata._id = undefined;
      this.pipelineCanvasMetadata._rev = undefined;
      request = this.pipelineCanvasService.addPipelineCanvasMetadata(this.pipelineCanvasMetadata);
    }

    request.subscribe();
  }

  afterStorage(statusMessage: Message, switchTab, pipelineId?: string) {
    this.hide();
    this.editorService.makePipelineAssemblyEmpty(true);
    this.editorService.removePipelineFromCache().subscribe();
    if (this.ShepherdService.isTourActive()) {
      this.ShepherdService.hideCurrentStep();
    }
    if (switchTab && !this.startPipelineAfterStorage) {
      this.Router.navigate(["pipelines"]);
    }
    if (this.startPipelineAfterStorage) {
      this.Router.navigate(["pipelines"], { queryParams: {pipeline: pipelineId}});
    }
  }

  hide() {
    this.dialogRef.close();
  };
}
