/*
 Created as part of the StratusLab project (http://stratuslab.eu),
 co-funded by the European Commission under the Grant Agreement
 INSFO-RI-261552.

 Copyright (c) 2010, Centre Nationale de la Recherche Scientifique

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package eu.stratuslab.authn;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

public class AuthnData {

    // Authorized users with associated groups.
    private Map<String, List<String>> data = new HashMap<String, List<String>>();

    // Empty list for nonexistent users.
    private static List<String> noGroups = createGroupList(null);

    public AuthnData(Object filename) {
        readAuthzFile(filename);
    }

    public boolean isValidUser(String username) {
        return data.containsKey(username);
    }

    public List<String> groups(String username) {
        return data.containsKey(username) ? data.get(username) : noGroups;
    }

    private void readAuthzFile(Object filename) {

        Properties properties = loadProperties(filename);

        for (Entry<Object, Object> entry : properties.entrySet()) {
            processEntry(entry);
        }

    }

    private void processEntry(Entry<Object, Object> entry) {

        Object key = entry.getKey();
        List<String> groupList = createGroupList(entry.getValue());

        data.put(key.toString(), groupList);

    }

    private static Properties loadProperties(Object filename) {

        Properties properties = new Properties();

        try {
            if (filename != null) {
                FileReader reader = new FileReader(filename.toString());
                properties.load(reader);
            }
        } catch (FileNotFoundException consumed) {
        } catch (IOException consumed) {
        }

        return properties;
    }

    private static List<String> createGroupList(Object value) {

        List<String> groupList = new ArrayList<String>();

        if (value != null) {
            String[] groups = value.toString().split("[\\s,]+");
            for (String group : groups) {
                if (!"".equals(group)) {
                    groupList.add(group);
                }
            }
        }

        return Collections.unmodifiableList(groupList);
    }

}
