/*
 * Copyright (C) 2021 Google Inc.
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

package com.google.inject.internal.aop;

import java.lang.reflect.Method;

/**
 * {@link ClassDefiner} that defines classes using {@code sun.misc.Unsafe#defineAnonymousClass}.
 *
 * @author mcculls@gmail.com (Stuart McCulloch)
 */
final class AnonymousClassDefiner implements ClassDefiner {

  private static final sun.misc.Unsafe THE_UNSAFE;
  private static final Method ANONYMOUS_DEFINE_METHOD;

  static {
    try {
      THE_UNSAFE = UnsafeGetter.getUnsafe();
      // defineAnonymousClass was removed in JDK17, so we must refer to it reflectively.
      ANONYMOUS_DEFINE_METHOD =
          sun.misc.Unsafe.class.getMethod(
              "defineAnonymousClass", Class.class, byte[].class, Object[].class);
    } catch (ReflectiveOperationException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  @Override
  public Class<?> define(Class<?> hostClass, byte[] bytecode) throws Exception {
    return (Class<?>) ANONYMOUS_DEFINE_METHOD.invoke(THE_UNSAFE, hostClass, bytecode, null);
  }
}
