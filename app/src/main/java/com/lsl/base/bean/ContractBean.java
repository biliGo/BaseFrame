package com.lsl.base.bean;

import java.util.List;

/**
 * Created by Forrest
 * on 2017/7/13 12:04
 */

public class ContractBean {
//    "push": [
//    {
//        "id": 1,
//            "name": "张三",
//            "cell": "13888888888",
//            "createdAt": 1499773030000,
//            "updatedAt": 1499773030000
//    },
//    {
//        "id": 2,
//            "name": "李四",
//            "cell": "13999999999",
//            "createdAt": 1499773030000,
//            "updatedAt": 1499773030000
//    }

    private List<Contract> push;

    public List<Contract> getPush() {
        return push;
    }

    public void setPush(List<Contract> push) {
        this.push = push;
    }

    public class Contract{
        int id;
        String name;
        String cell;
        long createdAt;
        long updatedAt;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCell() {
            return cell;
        }

        public void setCell(String cell) {
            this.cell = cell;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }

        public long getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(long updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
