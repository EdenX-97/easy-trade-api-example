/**
 * @author: Hongzhang Liu
 * @description 摩托车样本集合的dao层
 * @date 29/6/2022 7:16 pm
 */
package com.easytrade.easytradeapi.repository;

import com.easytrade.easytradeapi.model.ExampleMotor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ExampleMotorRepository extends MongoRepository<ExampleMotor, ObjectId> {

    public ExampleMotor findOneByModel(String model);

    public Boolean existsExampleMotorById(ObjectId id);

    public Boolean existsExampleMotorByModel(String model);

    public ExampleMotor findOneById(ObjectId id);


}
