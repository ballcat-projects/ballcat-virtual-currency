/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.tron.tronj.crypto.tuweniTypes;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;

import io.vertx.core.buffer.Buffer;

final class MutableBufferWrappingBytes extends BufferWrappingBytes implements MutableBytes {

	MutableBufferWrappingBytes(Buffer buffer) {
		super(buffer);
	}

	MutableBufferWrappingBytes(Buffer buffer, int offset, int length) {
		super(buffer, offset, length);
	}

	@Override
	public void set(int i, byte b) {
		buffer.setByte(i, b);
	}

	@Override
	public void setInt(int i, int value) {
		buffer.setInt(i, value);
	}

	@Override
	public void setLong(int i, long value) {
		buffer.setLong(i, value);
	}

	@Override
	public MutableBytes mutableSlice(int i, int length) {
		int size = size();
		if (i == 0 && length == size) {
			return this;
		}
		if (length == 0) {
			return MutableBytes.EMPTY;
		}

		checkElementIndex(i, size);
		checkArgument(i + length <= size,
				"Provided length %s is too big: the value has size %s and has only %s bytes from %s", length, size,
				size - i, i);

		return new MutableBufferWrappingBytes(buffer.slice(i, i + length));
	}

	@Override
	public Bytes copy() {
		return Bytes.wrap(toArray());
	}

	@Override
	public MutableBytes mutableCopy() {
		return MutableBytes.wrap(toArray());
	}

}