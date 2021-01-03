import { SvelteTypedComponent, SvelteAllProps } from 'svelte-typed-component';

export default class Dialog extends SvelteTypedComponent<DialogProps, DialogEvents, DialogSlots> {
}

declare const _DialogProps: {

    /** 
     * If true, dialog is visible. 
     */
    value: boolean;

} & SvelteAllProps;

declare const _DialogEvents: {
};

declare const _DialogSlots: {
    title: {};
    default: {};
    actions: {};
};
export declare type DialogProps = typeof _DialogProps;
export declare type DialogEvents = typeof _DialogEvents;
export declare type DialogSlots = typeof _DialogSlots;
export {
};
