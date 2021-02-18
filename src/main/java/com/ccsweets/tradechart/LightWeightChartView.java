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

import com.seomse.commons.utils.FileUtil;
import com.seomse.commons.utils.time.DateUtil;
import com.seomse.trading.technical.analysis.candle.CandleStick;

import java.io.File;
import java.util.List;

public class LightWeightChartView {
    public enum ChartDateType {MINUTE,DAY}
    CandleStick[] candleStickArr;
    StringBuilder createChartStr = new StringBuilder();
    ChartDateType dateType;
    public LightWeightChartView(CandleStick[] candleStickArr){
        this(candleStickArr,600,300,ChartDateType.DAY);
    }
    public LightWeightChartView(CandleStick[] candleStickArr , int width , int height , ChartDateType dateType){
        this.candleStickArr = candleStickArr;
        this.dateType = dateType;

        //createChartStr.append("<script>").append(fileContents).append("</script>\n");

        createChartStr.append( """
                var chart = LightweightCharts.createChart(document.body, {
                    width: %d,
                  height: %d,
                  rightPriceScale: {
                    visible: true,
                    borderColor: 'rgba(197, 203, 206, 1)',
                  },
                  leftPriceScale: {
                    visible: true,
                    borderColor: 'rgba(197, 203, 206, 1)',
                  },
                  layout: {
                    backgroundColor: '#ffffff',
                    textColor: 'rgba(33, 56, 77, 1)',
                  },
                  grid: {
                    horzLines: {
                      color: '#F0F3FA',
                    },
                    vertLines: {
                      color: '#F0F3FA',
                    },
                  },
                  crosshair: {
                    mode: LightweightCharts.CrosshairMode.Normal,
                  },
                  timeScale: {
                    borderColor: 'rgba(197, 203, 206, 1)',
                  },
                  handleScroll: {
                    vertTouchDrag: false,
                  },
                });
                
                const candlestickSeries = chart.addCandlestickSeries({
                  priceScaleId: 'left'
                });
                """.formatted(width,height));
        createChartStr.append("candlestickSeries.setData([");
        int candleStickArrSize = candleStickArr.length;
        for (int i = 0; i < candleStickArrSize; i++) {
            CandleStick candleStick = candleStickArr[i];
            double open = candleStick.getOpen();
            double close = candleStick.getClose();
            double low = candleStick.getLow();
            double high = candleStick.getHigh();
            long openTime = candleStick.getOpenTime();
            String timeStr;
            if(dateType.equals(ChartDateType.DAY)){
                timeStr  = DateUtil.getDateYmd(candleStick.getOpenTime(),"yyyy-MM-dd");
            } else {
                timeStr  = DateUtil.getDateYmd(candleStick.getOpenTime(),"yyyy-MM-dd HH:mm");
            }
            createChartStr.append("""
                    {
                        close: %.2f,
                        high: %.2f,
                        low: %.2f,
                        open: %.2f,
                        time: '%s'
                      },
                    """.formatted(
                    close,high,low,open,timeStr
            ));
        }
        createChartStr.setLength(createChartStr.length()-1);
        createChartStr.append("]);\n");
    }

    public void addMarkerAll(MarkerData[] markerDataArray) {
        createChartStr.append("""
              var markers = [];
                """);

        for (MarkerData markerData : markerDataArray) {
            String timeStr;
            if(dateType.equals(ChartDateType.DAY)){
                timeStr  = DateUtil.getDateYmd(markerData.getTime(),"yyyy-MM-dd");
            } else {
                timeStr  = DateUtil.getDateYmd(markerData.getTime(),"yyyy-MM-dd HH:mm");
            }
            createChartStr.append("""
                markers.push({ time: '%s', position: '%s', color: '%s', shape: '%s', text: '%s'});
                """
            .formatted(timeStr,markerData.getMarkerType().name(),markerData.getColor(),markerData.getMarkerShape().name(),markerData.getText())
            );
        }
        createChartStr.append("candlestickSeries.setMarkers(markers);");
    }

    public void addVolumeAll(VolumeData[] volumeDataArr, double topMargin , double bottomMargin){
        createChartStr.append("""
                var volumeSeries = chart.addHistogramSeries({
                  	color: '#26a69a',
                  	priceFormat: {
                  		type: 'volume',
                  	},
                  	priceScaleId: '',
                  	scaleMargins: {
                  		top: %.1f,
                  		bottom: %.1f,
                  	},
                  });
                  volumeSeries.setData([
                """.formatted(topMargin,bottomMargin));
        int volumeDataArrSize = volumeDataArr.length;
        for (int i = 0; i < volumeDataArrSize; i++) {
            VolumeData volumeData = volumeDataArr[i];
            String timeStr;
            if(dateType.equals(ChartDateType.DAY)){
                timeStr  = DateUtil.getDateYmd(volumeData.getTime(),"yyyy-MM-dd");
            } else {
                timeStr  = DateUtil.getDateYmd(volumeData.getTime(),"yyyy-MM-dd HH:mm");
            }

            createChartStr.append("""
                { time: '%s', value: %.2f, color: '%s' },
                """.formatted(timeStr,volumeData.getVolume() , volumeData.getColor()));
        }
        createChartStr.append("]);");
    }


    public void addLineAll(LineData[] lineDataArr ,String color, int size){
        createChartStr.append("""
                chart.addLineSeries({
                  color: '%s',
                  lineWidth: %d,
                }).setData([
                """.formatted(color,size));
        int lineDataArrSize = lineDataArr.length;
        for (int i = 0; i < lineDataArrSize; i++) {
            LineData lineData = lineDataArr[i];
            long openTime = lineData.getTime();
            double price = lineData.getPrice();


            String timeStr;
            if(dateType.equals(ChartDateType.DAY)){
                timeStr  = DateUtil.getDateYmd(lineData.getTime(),"yyyy-MM-dd");
            } else {
                timeStr  = DateUtil.getDateYmd(lineData.getTime(),"yyyy-MM-dd HH:mm");
            }
            createChartStr.append("""
                    {
                        time: '%s',
                        value: %.2f
                      },
                    """.formatted(
                    timeStr,price
            ));

        }

        createChartStr.setLength(createChartStr.length()-1);
        createChartStr.append("]);\n");
    }
    public String getHtml(){
        StringBuilder result = new StringBuilder("""
                <!DOCTYPE html>
                <html>
                <head>
                  <title>Seomse LightWeight-Chart View</title>
                  <script src="http://pure.github.io/pure/libs/pure.js"></script>
                </head>
                <body>
                """
        );

        result.append("<script src='https://unpkg.com/lightweight-charts/dist/lightweight-charts.standalone.production.js'></script>\n");
        result.append("<script>\n").append(createChartStr.toString()).append("\n</script>\n");

        return result.append("</body></html>").toString();
    }
    public static void main(String [] args){

    }
}
