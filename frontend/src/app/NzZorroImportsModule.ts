import { NgModule } from "@angular/core";
import { NzLayoutModule } from "ng-zorro-antd/layout";
import { NzButtonModule } from "ng-zorro-antd/button";
import { NzInputModule } from "ng-zorro-antd/input";
import { NzFormModule } from "ng-zorro-antd/form";
import { NzGridModule } from "ng-zorro-antd/grid";
import { NzSpinModule } from "ng-zorro-antd/spin";
import { NzMessageModule } from "ng-zorro-antd/message";
import { NzSelectModule } from "ng-zorro-antd/select";
import {NzTimePickerModule} from "ng-zorro-antd/time-picker";
import { NzDatePickerModule } from "ng-zorro-antd/date-picker";
import {NzTableModule} from "ng-zorro-antd/table";

@NgModule({
    exports: [
        // Ant Design modules
        NzLayoutModule,
        NzButtonModule,
        NzInputModule,
        NzFormModule,
        NzGridModule,
        NzSpinModule,
        NzMessageModule,
        NzSelectModule,
        NzTimePickerModule,
        NzDatePickerModule,
        NzTableModule
    ]})

export class NzZorroImportsModule {}