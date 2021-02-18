/*
 * Copyright (C) 2020 Seomse Inc.
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
package com.ccsweets.tradechart;

public class MarkerData {
    public static enum MarkerType{belowBar,aboveBar,inBar}
    public static enum MarkerShape{circle ,square , arrowUp , arrowDown }
    private long time;
    private String color;
    private String text;
    private String id;
    private MarkerType markerType;
    private MarkerShape markerShape;

    public long getTime() {
        return time;
    }

    public String getColor() {
        return color;
    }

    public String getText() {
        return text;
    }

    public String getId() {
        return id;
    }

    public MarkerType getMarkerType() {
        return markerType;
    }

    public MarkerShape getMarkerShape() {
        return markerShape;
    }

    public MarkerData(long time, String color, String text, String id, MarkerType markerType, MarkerShape markerShape) {
        this.time = time;
        this.color = color;
        this.text = text;
        this.id = id;
        this.markerType = markerType;
        this.markerShape = markerShape;
    }
}
