/*
 * Copyright 2017 Monterey Bay Aquarium Research Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.mbari.vars.annotation.messaging

import io.reactivex.subjects.{PublishSubject, Subject}
import org.mbari.vars.annotation.model.Annotation


sealed trait PublisherMessage[A] {
  def content: A
}

/**
 * Send when a new annotation is created or an existing one is updated
 * @param content
 */
case class AnnotationMessage(content: Annotation)
  extends PublisherMessage[Annotation]


/**
 * This is the shared message bus. All publishers whould listen to this bus and
 * publish the appropriate events to their subscribers.
 */
object MessageBus {

  val RxSubject: Subject[PublisherMessage[Any]] =
    PublishSubject.create[PublisherMessage[Any]]().toSerialized

}
