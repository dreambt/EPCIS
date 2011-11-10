/*
 *
 *  Copyright (c) 2002-2011, im47.cn All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

package org.fosstrak.epcis.captureclient;

/**
 * This Exception indicates that the CaptureClient encountered a problem while
 * trying to send a request to the EPCIS capture interface.
 */
public class CaptureClientException extends Exception {

    private static final long serialVersionUID = 4034170925462066270L;

    public CaptureClientException() {
        super();
    }

    public CaptureClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaptureClientException(String message) {
        super(message);
    }

    public CaptureClientException(Throwable cause) {
        super(cause);
    }
}