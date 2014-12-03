/*
 * Copyright (C) 2014 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dagger.internal.codegen;

import com.google.auto.value.AutoValue;
import dagger.MembersInjector;
import dagger.internal.codegen.writer.ClassName;
import dagger.internal.codegen.writer.ParameterizedTypeName;
import dagger.internal.codegen.writer.TypeNames;
import javax.inject.Provider;

/**
 * A value object that pairs a {@link Key} with a framework class (e.g.: {@link Provider},
 * {@link MembersInjector}) related to that key.
 *
 *  @author Gregory Kick
 *  @since 2.0
 */
@AutoValue
abstract class FrameworkKey {
  /**
   * The aspect of the framework for which a {@link Key} is an identifier. Particularly, whether a
   * key is for a {@link Provider} or a {@link MembersInjector}.
   */
  enum Kind {
    PROVIDER(Provider.class),
    MEMBERS_INJECTOR(MembersInjector.class),
    ;

    private final Class<?> frameworkClass;

    Kind(Class<?> frameworkClass) {
      this.frameworkClass = frameworkClass;
    }

    Class<?> frameworkClass() {
      return frameworkClass;
    }
  }

  // TODO(user): Pass instructions for how to handle requests for instances, since producers will
  // handle them differently.
  static FrameworkKey forDependencyRequest(DependencyRequest request) {
    switch (request.kind()) {
      case INSTANCE:
      case PROVIDER:
      case LAZY:
        return new AutoValue_FrameworkKey(Kind.PROVIDER, request.key());
      case MEMBERS_INJECTOR:
        return new AutoValue_FrameworkKey(Kind.MEMBERS_INJECTOR, request.key());
      default:
        throw new AssertionError();
    }
  }

  ParameterizedTypeName frameworkType() {
    return ParameterizedTypeName.create(
        ClassName.fromClass(kind().frameworkClass()), TypeNames.forTypeMirror(key().type()));
  }

  abstract Kind kind();
  abstract Key key();
}
