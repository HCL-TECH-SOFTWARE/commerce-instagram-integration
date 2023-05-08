/**
*-----------------------------------------------------------------
 Copyright [2022] [HCL America, Inc.]

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.

*-----------------------------------------------------------------
**/

INSERT INTO STORECONF (STOREENT_ID, NAME, VALUE) VALUES (0, 'instagram.api', 'https://graph.facebook.com/v15.0/');
INSERT INTO STORECONF (STOREENT_ID, NAME, VALUE) VALUES (0, 'instagram.accounts.api', 'me/accounts');
INSERT INTO STORECONF (STOREENT_ID, NAME, VALUE) VALUES (0, 'instagram.insta.id.api', '&fields=instagram_business_account');
INSERT INTO STORECONF (STOREENT_ID, NAME, VALUE) VALUES (0, 'instagram.media.api', '/media');
INSERT INTO STORECONF (STOREENT_ID, NAME, VALUE) VALUES (0, 'instagram.publish.api', '/media_publish');
INSERT INTO STORECONF (STOREENT_ID, NAME, VALUE) VALUES (0, 'instagram.quota.usage.api', '/content_publishing_limit');
INSERT INTO STORECONF (STOREENT_ID, NAME, VALUE) VALUES (0, 'instagram.quota.usage.api.fields', '&fields=quota_usage,config');
INSERT INTO STORECONF (STOREENT_ID, NAME, VALUE) VALUES (0, 'instagram.quota.limit', 5);
INSERT INTO STORECONF (STOREENT_ID, NAME, VALUE) VALUES (0, 'instagram.alt.image.limit', 2);
INSERT INTO STORECONF (STOREENT_ID, NAME, VALUE) VALUES (0, 'instagram.app.id', '6284770401552828');
INSERT INTO STORECONF (STOREENT_ID, NAME, VALUE) VALUES (0, 'instagram.api.version', 'v15.0');