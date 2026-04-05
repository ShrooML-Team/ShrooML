package com.shrooml.services.api;

import java.util.List;
import java.util.Map;

public class ValidationError {
    private List<ValidationDetail> detail;

    public List<ValidationDetail> getDetail() {
        return detail;
    }

    public void setDetail(List<ValidationDetail> detail) {
        this.detail = detail;
    }
    public static class ValidationDetail{
        private List<Object> loc;
        private String msg;
        private String type;
        private Object input;
        private Map<String,Object> ctx;

        public void setCtx(Map<String, Object> ctx) {
            this.ctx = ctx;
        }

        public void setInput(Object input) {
            this.input = input;
        }

        public void setLoc(List<Object> loc) {
            this.loc = loc;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public List<Object> getLoc() {
            return loc;
        }

        public Map<String, Object> getCtx() {
            return ctx;
        }

        public Object getInput() {
            return input;
        }

        public String getMsg() {
            return msg;
        }

        public String getType() {
            return type;
        }
    }
}
