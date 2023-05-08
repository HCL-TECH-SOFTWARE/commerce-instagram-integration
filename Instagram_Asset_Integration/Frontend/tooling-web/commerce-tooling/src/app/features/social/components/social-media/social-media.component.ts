/*
*==================================================
Copyright [2022] [HCL America, Inc.]
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*================================================== 
*/


import { Component, OnInit, OnDestroy, ViewChild, AfterViewInit, Inject,ElementRef, HostListener} from "@angular/core";
import { Router } from "@angular/router";
import { Subject, Subscription, forkJoin } from "rxjs";
import { debounceTime } from "rxjs/operators";
import { FormGroup, FormControl } from "@angular/forms";
import { SearchMainService } from "../../../search/services/search-main.service";
import { ApiConfiguration } from "../../../../rest/api-configuration";
import { V2DataStatusService } from "../../../../rest/services/v2data-status.service";
import { LanguageService } from "../../../../services/language.service";
import { CurrentUserService } from "../../../../services/current-user.service";
import { OnlineStoresService } from "../../../../rest/services/online-stores.service";
import { MatChipInputEvent } from "@angular/material/chips";
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import { FormBuilder } from '@angular/forms';
import { SocialMainService } from "../../services/social-main.service";
import {MatDialog, MatDialogRef, MAT_DIALOG_DATA} from '@angular/material/dialog';
import { TranslateService } from "@ngx-translate/core";
import { AlertService } from "../../../../services/alert.service";
import { stringify } from "querystring";
import { DomSanitizer } from '@angular/platform-browser';
import { MatIconRegistry } from '@angular/material/icon';
import { MatTreeFlatDataSource, MatTreeFlattener } from "@angular/material/tree";
import { FlatTreeControl } from "@angular/cdk/tree";
import { Category, PageBuilderMainService } from "../../../../../app/features/page-builder/services/page-builder-main.service";
import { MatTabChangeEvent } from "@angular/material/tabs";
import { MatTabGroup } from '@angular/material/tabs';


declare const FB: any;

interface productDetail {
	thumbnail: string;
	name:string;
	description:string;
	hashtags:Array<string>;
	partNumber:string;
}
interface CategoryNode {
	expandable: boolean;
	category: Category;
	level: number;
}

export interface PageBuilderSettings {
	store?: any;
	languageIds?: Array<number>;
	defaultLanguageId?: number;
	currencies?: Array<string>;
	defaultCurrency?: string;
}


@Component({
	selector: "social-media",
	templateUrl: "./social-media.component.html",
	styleUrls: ["./social-media.component.scss"]
})
export class SocialMediaComponent implements OnInit, OnDestroy, AfterViewInit {
	addOnBlur = true;
	removable = true;
	selectable = true;
	storeForm: FormGroup;
	store: FormControl;
	showFilters = false;
	showSearchClearMessage = false;
	showLoader = false;
	storeList: Array<any> = [];
	showButtons= false;
	maximumCharacters:number = 2100;
	maximumHashtags:number = 30;
	showInvalidCharacterLength = false;
    readonly separatorKeysCodes:number[] = [ENTER, COMMA];
	socialMediaButtons = this.formBuilder.group({
	    instagram: false,
		facebook: true,
		whatsapp: true,
	  });
	showMultipleImages=false;
	@ViewChild("filters") filters: ElementRef<HTMLDivElement>;
	@ViewChild("searchInput") searchInput: ElementRef;
	@ViewChild(MatTabGroup) tabGroup: MatTabGroup;
	listSearchForm: FormGroup;
	searchTerm: FormControl;
	currentSearchTerm = null;
	selectedStore = null;
	currency = null;
	language = null;
	products = null;
	currentProducts = null;
	currentSearchIndexStatus = null;
	showProductDetailPage = false;
	selectedCategoryId: string = null;
	selectedCategoryName: string = null;
	productDetail:productDetail = {
		thumbnail: "",
		name: "",
		description: "",
		hashtags: [],
		partNumber:""
	};
	images: Array<any> = [];
	hashtagString = null;
	currencyList: Array<any> = [];
	languageList: Array<any> = [];
	selectedCount: number= 0;
	isDisabledPublish: boolean = false;
	showBreadcrumb:boolean = false;
	accessToken: string;
	enteredCharacters: number;
	getStoreId;
	parentBreadcrumbName:string="";
	childBreadcrumbName:string="";

	
	private currencyDisplayNames = {};
	private languageDisplayNames = {};
	private onPageBuilderSettingsSubscription: Subscription = null;
	private onSearchSettingsSubscription: Subscription = null;
	private storeSearchString: Subject<string> = new Subject<string>();
	private getStoresSubscription: Subscription = null;
	private getProductsSubscription: Subscription = null;
	private getStoreNameSubscription: Subscription = null;
	private getStoreConfigurationSubscription: Subscription = null;
	private searchString: Subject<string> = new Subject<string>();
	private categories: Array<Category> = [];
	private getCategoriesSubscription: Subscription = null;
	
	
	categoryTreeControl = new FlatTreeControl<CategoryNode>(node => node.level, node => node.expandable);
	categoryDataSource = new MatTreeFlatDataSource(this.categoryTreeControl,
		new MatTreeFlattener((category: Category, level: number) => {
			return {
			expandable: category.children.length > 0,
			category,
			level
		};
	}, node => node.level, node => node.expandable, node => node.children));
	
	
	
	constructor(private router: Router,
		private formBuilder: FormBuilder,
		private searchMainService: SearchMainService,
		private apiConfiguration: ApiConfiguration,
		private onlineStoresService: OnlineStoresService,
		private v2DataStatusService: V2DataStatusService,
		private currentUserService: CurrentUserService,
		public _socialMainService: SocialMainService,
		private matIconRegistry: MatIconRegistry,
		private domSanitizer: DomSanitizer,
		private pageBuilderMainService: PageBuilderMainService,
		public dialog: MatDialog) {
		v2DataStatusService.rootUrl = apiConfiguration.searchDataQueryRootUrl;
		this.matIconRegistry.addSvgIcon(
			'instagram-icon',
			this.domSanitizer.bypassSecurityTrustResourceUrl('assets/social-media-icons/instagram.svg')
		);
		this.matIconRegistry.addSvgIcon(
			'facebook-icon',
			this.domSanitizer.bypassSecurityTrustResourceUrl('assets/social-media-icons/facebook.svg')
		);
		this.matIconRegistry.addSvgIcon(
			'whatsapp-icon',
			this.domSanitizer.bypassSecurityTrustResourceUrl('assets/social-media-icons/whatsapp.svg')
		);
	}
				
	hasChild = (_: number, _nodeData: CategoryNode) => _nodeData.expandable;
				
	ngOnInit() {
		this.createFormControls();
		this.createForm();
		this.storeSearchString.pipe(debounceTime(250)).subscribe(searchString => {
			this.getStores(searchString);
		});
		this.searchString.pipe(debounceTime(250)).subscribe((searchTerm) => {
			this.currentSearchTerm = searchTerm;
			this.getSearchIndexStatus();
			this.getProducts();
		});
	}
	
	ngAfterViewInit() {
		this.searchInput.nativeElement.focus();
		this.getStoreNameSubscription = this.currentUserService.getStoreName().subscribe((storeName:string) => {
			if (storeName) {
				this.onlineStoresService.getOnlineStoresByIdentifier({
					identifier: storeName,
					usage: "HCL_PageBuilderTool",
					limit: 1
				}).subscribe((onlineStoreResponse) => {
					if (this.getStoreNameSubscription) {
						this.getStoreNameSubscription.unsubscribe();
						this.getStoreNameSubscription = null;
					}
					const storeArray = onlineStoreResponse.items;
					for (let i = 0; i < storeArray.length; i++) {
						const store = storeArray[i];
						if (storeName === store.identifier) {
							this.selectStore(store);
							break;
						}
					}
					if (this.selectedStore === null) {
						this.getFirstStore();
					}
				});
			} 
			else {
				this.getFirstStore();
			}
		});
		this.onPageBuilderSettingsSubscription = this.pageBuilderMainService.onPageBuilderSettings
			.subscribe((pageBuilderSettings: PageBuilderSettings) => {
				this.getCategories();
		});
		this.getCategories();
	}
	


	private getCategories() {
		const store = this.pageBuilderMainService.pageBuilderSettings.store;
		if (store) {
			const args: any = {
                depthAndLimit: ["11,11"]
			};
			if (this.getCategoriesSubscription != null) {
				this.getCategoriesSubscription.unsubscribe();
				this.getCategoriesSubscription = null;
			};
			this.getCategoriesSubscription = this.pageBuilderMainService.createGetCategoriesRequest(args).subscribe((body: any) => {
				this.getCategoriesSubscription.unsubscribe();
				this.getCategoriesSubscription = null;
				const newCategories = [];
				if (body.contents) {
					this.populateCategories(body.contents, newCategories);
				}
				this.categories = newCategories;
				this.categoryDataSource.data = this.categories;
			},
			error => {
				console.log("error",error)
			});
		}
	}
	
	private populateCategories(sourceList:any, targetList:any) {
		sourceList.forEach((sourceCategory:any) => {
			const category: Category = {
				id: sourceCategory.id,
				identifier: sourceCategory.identifier,
				thumbnail: sourceCategory.thumbnail ? (this.pageBuilderMainService.imageRoot + sourceCategory.thumbnail) : sourceCategory.thumbnail,
				name: sourceCategory.name,
				children: []
			};
			if (sourceCategory.children) {
				this.populateCategories(sourceCategory.children, category.children);
			}
			targetList.push(category);
		});
	}

	onTabClick(event: MatTabChangeEvent) {
		if(event.index === 0){
			this.categoryTreeControl.collapseAll();
			this.currentProducts = [];
			this.selectedCategoryId = null;
			this.selectedCategoryName = null;
			this.showProductDetailPage = false;
			this.showBreadcrumb = false;
		}
		else if(event.index === 1){
			this.clearSearchTerm();
			this.showSearchClearMessage = false;
		}
	}

	parentCategoryList() {
		this.categoryTreeControl.collapseAll();
		this.showBreadcrumb = false;
	}	  

	toggleCategorySelection(category:any,node?:any) {
		if (this.selectedCategoryId === category.id) {
			this.selectedCategoryId = null;
			this.selectedCategoryName = null;
		} else {
			this.currentProducts = [];
			this.selectedCategoryId = category.id;
			this.selectedCategoryName = category.name;
			this.getProducts();
		}
		const filterNode = this.categoryTreeControl.dataNodes.filter(i => i === node);
		if (filterNode.length > 0) {
			this.showBreadcrumb = true;
			const parentNode = this.categoryTreeControl.expansionModel.selected;
			const parentName = [...parentNode].map(i => i.category.identifier)
			this.parentBreadcrumbName = parentName.join('');	
		}
		this.childBreadcrumbName = node.category.name;

	}
	
	resetSelectedStore() {
		if (this.selectedStore) {
			this.store.setValue(this.selectedStore.identifier);
			this.clearSearchTerm();
			this.showSearchClearMessage = false;
		}
	}
	
	getTotalCharacters():number {
		this.showInvalidCharacterLength = false;
		this.enteredCharacters = this.productDetail.name ? this.productDetail.name.length : 0;
		this.enteredCharacters+= this.productDetail.description ? this.productDetail.description.length : 0;
		this.enteredCharacters+= this.productDetail.hashtags.join('').length;
		this.hashtagString = this.productDetail.hashtags.join('');
		if (this.enteredCharacters > this.maximumCharacters) {
            this.showInvalidCharacterLength = true;
		}
		return this.enteredCharacters;
	}

	setFilterVisibility($event:any, visibility:any) {
		if (visibility) {
			this.showFilters = true;
			setTimeout(() => {
				this.filters.nativeElement.focus();
			}, 200);
		} else {
			if ($event.relatedTarget === null) {
				this.showFilters = false;
			}
		}
	}


	toggleShowFilters() {
		this.showFilters = !this.showFilters;
		if (this.showFilters) {
			setTimeout(() => {
				this.filters.nativeElement.focus();
			}, 200);
		}
	}


	getStores(searchString: string) {
		if (this.getStoresSubscription != null) {
			this.getStoresSubscription.unsubscribe();
			this.getStoresSubscription = null;
		}
		this.getStoresSubscription = this.onlineStoresService.getOnlineStoresByIdentifier({
			usage: "HCL_PageBuilderTool",
			identifier: "*" + searchString + "*",
			limit: 10
		}).subscribe(response => {
			this.getStoresSubscription = null;
			if (response.items.length === 1 && response.items[0].identifier === this.store.value) {
				this.selectStore(response.items[0]);
			} else {
				this.storeList = response.items;
			}
		},
		error => {
			this.getStoresSubscription = null;
		});
	}


	showProductDetails(product: any) {
		this.getStoreId = this.searchMainService.searchSettings.store.id;
		this.showLoader = false;
		this.productDetail = product;
		this.showMultipleImages = false;
		this.showProductDetailPage = true;
		this.images = [];
		this.socialMediaButtons.value.instagram = false;
		this.showButtons = false;
		this.selectedCount = 0;
		if(this.productDetail.hashtags){
			this.productDetail.hashtags = this.productDetail.hashtags;
		}
		else{
			this.productDetail.hashtags = [];
		}
	    this.getTotalCharacters();
		let partNumber = "";
		if (this.productDetail.partNumber === "LR-FNTR-0007") {
			this.showMultipleImages = true;
			partNumber = this.productDetail.partNumber;
			const args: any = {
				storeId: this.store,
				partNumber: [partNumber],
				catalogId: "11501",
				contractId: "-11005",
				langId: this.language
			};
			this.getProductsSubscription = this.searchMainService.createGetProductsRequest(args).subscribe((body: any) => {
				this.getProductsSubscription.unsubscribe();
				this.getProductsSubscription = null;
				if (body.contents) {
					const item = body.contents[0];
					const images = item.images;
					const multipleImages = images.map((i: any) => i.thumbnail);
					const hostname = window.location.hostname;
					 const hostUrl = "https://" + hostname;
					//  const hostUrl = "https://commerce-preview.combda.hclcx.com";
					let imagepath = "";
					for (let i = 0; i < multipleImages.length; i++) {
						imagepath = hostUrl + multipleImages[i];
						this.images.push({
							mediaURL: imagepath,
							selected: false,
							mediaType : "image"
						})
					}
				}
			});
		}
	}


	selectImage(image:any) {
		image.selected = !image.selected;
		if (image.selected) {
		  this.selectedCount++;
		} else {
		  this.selectedCount--;
		}
		if (this.selectedCount >= 2) {
		  this.disableUnchecked();
		} else {
		  this.enableAll();
		}
	}

	disableUnchecked() {
		this.images.forEach(image => {
		  if (!image.selected) {
			image.disabled = true;
		  }
		});
	}
	
	enableAll() {
		this.images.forEach(image => {
		  image.disabled = false;
		});
	}


	clickInstaCheckbox(){
		if(this.socialMediaButtons.value.instagram){
			this.showButtons = true;	
		}
		else{
			this.showButtons = false;
		}
	}

	getSelectedImages(){
		return this.images.filter(image=>image.selected);
	}

	removeHashtag(hashtag: any) {
		const index = this.productDetail.hashtags.indexOf(hashtag);
		if (index >= 0) {
			this.productDetail.hashtags.splice(index, 1);
		}
		this.getTotalCharacters();
	}

	
	add(event: MatChipInputEvent): void {
		const input = event.input;
		const value = event.value.replace(/\s+/g, '');
		if((value ||'').trim()) {
			this.productDetail.hashtags.push(value);
			this.getTotalCharacters();
		}
		if(input){
			input.value = '';
		}
		if(this.productDetail.hashtags.length>this.maximumHashtags){
		   this.removeHashtag(value);
		   this.getTotalCharacters();
		}
		if(this.enteredCharacters>this.maximumCharacters){
			this.removeHashtag(value);
			this.getTotalCharacters();
			this.showInvalidCharacterLength = true;
		}
	}


	selectStore(store: any, languageId?: number, currency?: string) {
		if (this.getStoreNameSubscription) {
			this.getStoreNameSubscription.unsubscribe();
			this.getStoreNameSubscription = null;
		}
		if (this.getStoreConfigurationSubscription) {
			this.getStoreConfigurationSubscription.unsubscribe();
			this.getStoreConfigurationSubscription = null;
		}
		this.selectedStore = store;
		const currentStoreId = this.selectedStore ? this.selectedStore.id : null;
		this.currentUserService.setPreferredStore(store.identifier);
		this.store.setValue(store.identifier);
		this.pageBuilderMainService.setStore(store);
		if (currentStoreId !== store.id) {
			this.storeList = [];
			this.searchStores("");
		}
		const storeConfigurationRequests = [
			this.onlineStoresService.getOnlineStoreCurrencies(store.id),
			this.onlineStoresService.getOnlineStoreLanguages(store.id)
		];
		this.getStoreConfigurationSubscription = forkJoin(storeConfigurationRequests).subscribe((responseList: Array<any>) => {
			const newCurrencyList = [];
			let currencyFound = false;
			for (let i = 0; i < responseList[0].currencies.length; i++) {
				const code = responseList[0].currencies[i];
				const displayName = this.currencyDisplayNames[code] ? this.currencyDisplayNames[code] : code;
				newCurrencyList.push({
					code,
					displayName
				});
				if (currency === code) {
					currencyFound = true;
				}
			}
			if (!currencyFound) {
				currency = responseList[0].defaultCurrency;
			}
			this.currencyList = newCurrencyList;
			if (currency) {
				this.currency = currency;
			}
			const newLanguageList = [];
			let languageIdFound = false;
			for (let i = 0; i < responseList[1].languageIds.length; i++) {
				const id = responseList[1].languageIds[i];
				const displayName = this.languageDisplayNames[id] ? this.languageDisplayNames[id] : "";
				newLanguageList.push({
					id,
					displayName
				});
				if (languageId === id) {
					languageIdFound = true;
				}
			}
			if (!languageIdFound) {
				languageId = responseList[1].defaultLanguageId;
			}
			this.languageList = newLanguageList;
			if (languageId) {
				this.language = languageId;
			}
			this.searchMainService.setStore(store, currency, languageId);
		});
	}


	private getFirstStore() {
		this.onlineStoresService.getOnlineStores({
			usage: "HCL_PageBuilderTool",
			limit: 1
		}).subscribe((onlineStoreResponse) => {
			const storeArray = onlineStoreResponse.items;
			for (let index = 0; index < storeArray.length; index++) {
				const store = storeArray[index];
				console.log(store)
				this.selectStore(store);
				break;
			}
		});
	}


	getSearchIndexStatus() {
		if (this.searchMainService.searchSettings.store) {
			this.v2DataStatusService.getAllIngestControllerStatusResponse({
				storeId: this.searchMainService.searchSettings.store.id
			}).subscribe((response: any) => {
				this.currentSearchIndexStatus = Number(response.body?.status);
			}, (error) => {
				this.currentSearchIndexStatus = 1;
			});
		}
	}


	searchProducts(searchTerm: any) {
		this.searchString.next(searchTerm);
		this.showSearchClearMessage = false;
	}


	private createFormControls() {
		this.searchTerm = new FormControl(this.currentSearchTerm);
		this.store = new FormControl("");
	}


	private createForm() {
		this.listSearchForm = new FormGroup({
			searchTerm: this.searchTerm
		});
		this.storeForm = new FormGroup({
			store: this.store
		});
	}


	searchStores(searchString: string) {
		this.storeSearchString.next(searchString);
	}


	clearSearchTerm() {
		this.searchMainService.clearSearchTermData();
		this.currentSearchTerm = null;
		this.searchTerm.setValue("");
		this.products = [];
		this.showSearchClearMessage = true;
		this.showProductDetailPage = false;
		this.socialMediaButtons.reset();
	}


	private getProducts() {
		if (this.getProductsSubscription) {
			this.getProductsSubscription.unsubscribe();
			this.getProductsSubscription = null;
		}
		if (this.currentSearchTerm || this.selectedCategoryId) {
			const args: any = {};
			if (this.currentSearchTerm) {
				args.searchTerm = this.currentSearchTerm;
			}
			if (this.selectedCategoryId !== null) {
				args.categoryId = this.selectedCategoryId;
			}
			this.getProductsSubscription = this.searchMainService.createGetProductsRequest(args).subscribe((body: any) => {
				this.getProductsSubscription.unsubscribe();
				this.getProductsSubscription = null;
				const newProducts = [];
				if (body.contents) {
					for (let i = 0; i < body.contents.length; i++) {
						const item = body.contents[i];
						const name = item.name;
						const description = item.shortDescription;
						const partNumber = item.partNumber;
						const catalogEntryId = item.id;
						const type = item.type;
						const hostname = window.location.hostname;
						 const thumbnail = "https://" + hostname + item.thumbnail;
						// const thumbnail = "https://commerce-preview.combda.hclcx.com" + item.thumbnail;					
						newProducts.push({
							catalogEntryId,
							type,
							name,
							description,
							thumbnail,
							partNumber
						});
					}
				}
				if (this.currentSearchTerm) {
					this.products = newProducts;
				}
				if (this.selectedCategoryId !== null) {
					this.currentProducts = newProducts;
				}
			});
	    }
		else {
			this.products = [];
			this.currentProducts = [];
		}
	}

	//Method to invoke create media API
	public createMedia() {
		if(this.socialMediaButtons.value.instagram){
			const selectedImages = this.getSelectedImages();
			this.showLoader = true;
			let url = "create_media";
			let body = {
				 "product" : {
						"heroMediaURL" : this.productDetail.thumbnail,
						"heroMediaType" : "image",
						"title" : this.productDetail.name,
						"description" : this.productDetail.description,
						"hashTags" : this.productDetail.hashtags.join(' '),
						"alternateMedia" : selectedImages

			},
			"accessToken" : this.accessToken,
			"facebookPageID"  : sessionStorage.getItem("facebookPageID"),
			"instagramAccountId" : sessionStorage.getItem("instagramAccountId")
			};
			if (body.product.alternateMedia.length === 0){
				delete body.product.alternateMedia
			}
			if (!body.facebookPageID){
				delete body.facebookPageID
			}
			if (!body.instagramAccountId){
				delete body.instagramAccountId
			}
			this._socialMainService.postConfigration(url,body,this.searchMainService.searchSettings.store.id).subscribe(res=>{
				res.accessToken = this.accessToken;
				res.storeId = this.searchMainService.searchSettings.store.id;
				res.productName = this.productDetail.name;
				res.productImage = this.productDetail.thumbnail;
				res.productDesc = this.productDetail.description;
				res.productHash = this.productDetail.hashtags.toString();
				sessionStorage.setItem("facebookPageID", res.facebookPageID);
				sessionStorage.setItem("instagramAccountId", res.instagramAccountId);
				this.showLoader=false;
				this.openDialog(res);
			},err =>{			
				this.showLoader=false;
			})
	   }
	}

	//To show confirmation dialog box after create media API
	openDialog(param:any) {
		let dialogRef = this.dialog.open(socialConfirmPopup, {
			data: param,
		});

		dialogRef.afterClosed().subscribe(result => {
			if(result.event ==='save'){
				this.productDetail = {
					thumbnail: "",
					name: "",
					description: "",
					hashtags: [],
					partNumber:""
				};
				this.categoryTreeControl.collapseAll();
				this.showBreadcrumb = false;
				this.tabGroup.selectedIndex = 0;
				this.hashtagString = "";
				this.showProductDetailPage = false;
				this.clearSearchTerm();
				this.showSearchClearMessage = false;
				this.scrollToTop();
				this.searchInput.nativeElement.focus();
				this.socialMediaButtons.reset();
			}
		});
	}


	ngOnDestroy() {
		if (this.onSearchSettingsSubscription) {
			this.onSearchSettingsSubscription.unsubscribe();
			this.onSearchSettingsSubscription = null;
		}
		if (this.getProductsSubscription) {
			this.getProductsSubscription.unsubscribe();
			this.getProductsSubscription = null;
		}
		this.storeSearchString.unsubscribe();
	}

	loginStatusforFB(status:any){
		this.isDisabledPublish = status !== "true" ? true : false;
	}

	getAccessToken(accessToken:any){
		this.accessToken = accessToken;
	}

	//@HostListener('click', ['$event'])
	scrollToTop() {
	  window.scrollTo({ top: 0, behavior: 'smooth' });
	}

}

@Component({
	selector: 'social-confirmPopup',
	template: `
	<h1 mat-dialog-title class="dialog-title">Confirmation</h1>
	<div mat-dialog-content class="dialog-container">
		<div fxLayout="row" class="layout">
			<div class="product-details">
				<div class="dialog-product-name">
					<span>{{productName}}</span>
				</div>
				<div class="dialog-product-desc content-scroll">
					<div>
						{{(readMore) ? productDesc : productDesc | slice:0:50}} <span *ngIf="!readMore && productDesc.length > 50">...</span>
						<a href="javascript:;" *ngIf="productDesc.length > 50" (click)="readMore=!readMore">{{!readMore ? '[Read More]' : '[Read Less]'}}</a>
					</div>
				</div>
				<div class="dialog-product-hash content-scroll">
					<div>
						{{(readMoreDesc) ? productHash : productHash | slice:0:40}} <span *ngIf="!readMoreDesc && productHash.length > 40">...</span>
						<a href="javascript:;" *ngIf="productHash.length > 40" (click)="readMoreDesc=!readMoreDesc">{{!readMoreDesc ? '[Read More]' : '[Read Less]'}}</a>
					</div>
				</div>
			</div>
			<div class="dialog-image-div">
				<img class="dialog-product-image" src={{productImage}} alt="product-image">
			</div>
		</div>
	</div>
	<div mat-dialog-actions>
		<button mat-raised-button (click)="closeDailog()" class="close-dailog-btn">Cancel</button>
		<button mat-raised-button color="primary" class="yes-dailog-btn" cdkFocusInitial (click)="publishMedia()">Publish</button>
	</div>
	<div class="loader-section" *ngIf="showLoader">
		<mat-spinner></mat-spinner>
	</div>`,
	styleUrls: ["./social-media.component.scss"]
  })
  export class socialConfirmPopup {
	showLoader = false;
	readMore = false;
	readMoreDesc = false;
	productName:string=this.data.productName;
	productImage:string=this.data.productImage;
	productDesc:string=this.data.productDesc;
	productHash:string=this.data.productHash.replace(/,/g, " ");
	constructor(public dialogRef: MatDialogRef<socialConfirmPopup>,
		        private router: Router, 
				public _socialMainService:SocialMainService, 
				public dialog: MatDialog,
				private translateService: TranslateService,
				private alertService: AlertService,
				@Inject(MAT_DIALOG_DATA) public data: any) {}
	
	closeDailog(){
		this.dialog.closeAll();
	}			

	//To invoke publish_media API
	public publishMedia() {
		this.showLoader = true;
		let url = "publish_media";
		let body = {"creation_id" : this.data.containerId,
					"accessToken" : this.data.accessToken,
					"facebookPageID"  : sessionStorage.getItem("facebookPageID"),
					"instagramAccountId" : sessionStorage.getItem("instagramAccountId")
		};
		if (!body.facebookPageID){
			delete body.facebookPageID
		}
		if (!body.instagramAccountId){
			delete body.instagramAccountId
		}
		this._socialMainService.postConfigration(url,body,this.data.storeId).subscribe(res=>{
			this.showLoader = false;
			this.dialogRef.close({event:'save'});
			this.translateService.get("SOCIAL.PUBLISH_API_SUCCESS").subscribe((message: string) => {
			      this.alertService.success({ message });
			});
			
		},err=>{
			this.showLoader=false;
			this.dialog.closeAll();
		})
	}

	openDialog() {
		this.dialog.open(socialConfirmPopup);
	}
  }


