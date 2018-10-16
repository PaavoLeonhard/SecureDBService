/*
 * Copyright 2018 Information Systems Engineering, TU Berlin, Germany
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at    http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * This is being developed for the DITAS Project: https://www.ditas-project.eu/
 */

package de.tub.secureService.service;

import org.apache.log4j.Logger;

public class Log4jUtil {
    private static final Logger log =Logger.getLogger(Log4jUtil.class.getSimpleName());

    public Log4jUtil() {
    }
    public static Logger getLogger(){
        return log;
    }
}
