/*
 * Copyright 2018 (c) Andy Li, Colin Choi, James Sun, Jeremy Ng, Micheal Nguyen, Wyatt Praharenka
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package com.cmput301w18t05.taskzilla.request.command;


import com.cmput301w18t05.taskzilla.AppCache;
import com.cmput301w18t05.taskzilla.Bid;
import com.cmput301w18t05.taskzilla.controller.ElasticSearchController;
import com.cmput301w18t05.taskzilla.request.DeletionRequest;

 /**
  * Request for getting a task object from Elastic Search using the username of the provider
  * @author W
  * @see ElasticSearchController
  * @version 1.0
*/
 @Deprecated
public class RemoveBidRequest extends DeletionRequest{
    private Bid bid;

    public RemoveBidRequest(Bid bid) {
        this.bid = bid;
        queueReady = true;
    }

     /**
      * remove the bid with this id from the elasticsearch
      */
     public void execute(){
        ElasticSearchController.RemoveBid deleteRequest = new ElasticSearchController.RemoveBid();
        deleteRequest.execute(this.bid);
    }

     /**
      * remove it in the app cache if we are offline. note that this request
      * will complete when we come online.
      */
    @Override
    public void executeOffline() {
        AppCache.getInstance().removeBidByBidid(bid.getId());
    }

    @Override
    public boolean requiresConnection() {
        return false;
    }

    public void getResult() {
    }

}
